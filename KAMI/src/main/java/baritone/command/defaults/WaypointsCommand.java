/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.cache.IWaypoint;
import baritone.api.cache.Waypoint;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BetterBlockPos;
import baritone.api.command.Command;
import baritone.api.command.datatypes.ForWaypoints;
import baritone.api.command.datatypes.RelativeBlockPos;
import baritone.api.command.exception.CommandException;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.command.exception.CommandInvalidTypeException;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.helpers.Paginator;
import baritone.api.command.helpers.TabCompleteHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static baritone.api.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

public class WaypointsCommand extends Command {

    public WaypointsCommand(IBaritone baritone) {
        super(baritone, "waypoints", "waypoint", "wp");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        Action action = args.hasAny() ? Action.getByName(args.getString()) : Action.LIST;
        if (action == null) {
            throw new CommandInvalidTypeException(args.consumed(), "an action");
        }
        BiFunction<IWaypoint, Action, ITextComponent> toComponent = (waypoint, _action) -> {
            ITextComponent component = new TextComponentString("");
            ITextComponent tagComponent = new TextComponentString(waypoint.getTag().name() + " ");
            tagComponent.getStyle().setColor(TextFormatting.GRAY);
            String name = waypoint.getName();
            ITextComponent nameComponent = new TextComponentString(!name.isEmpty() ? name : "<empty>");
            nameComponent.getStyle().setColor(!name.isEmpty() ? TextFormatting.GRAY : TextFormatting.DARK_GRAY);
            ITextComponent timestamp = new TextComponentString(" @ " + new Date(waypoint.getCreationTimestamp()));
            timestamp.getStyle().setColor(TextFormatting.DARK_GRAY);
            component.appendSibling(tagComponent);
            component.appendSibling(nameComponent);
            component.appendSibling(timestamp);
            component.getStyle()
                    .setHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new TextComponentString("Click to select")
                    ))
                    .setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            String.format(
                                    "%s%s %s %s @ %d",
                                    FORCE_COMMAND_PREFIX,
                                    label,
                                    _action.names[0],
                                    waypoint.getTag().getName(),
                                    waypoint.getCreationTimestamp()
                            ))
                    );
            return component;
        };
        Function<IWaypoint, ITextComponent> transform = waypoint ->
                toComponent.apply(waypoint, action == Action.LIST ? Action.INFO : action);
        if (action == Action.LIST) {
            IWaypoint.Tag tag = args.hasAny() ? IWaypoint.Tag.getByName(args.peekString()) : null;
            if (tag != null) {
                args.get();
            }
            IWaypoint[] waypoints = tag != null
                    ? ForWaypoints.getWaypointsByTag(this.baritone, tag)
                    : ForWaypoints.getWaypoints(this.baritone);
            if (waypoints.length > 0) {
                args.requireMax(1);
                Paginator.paginate(
                        args,
                        waypoints,
                        () -> logDirect(
                                tag != null
                                        ? String.format("All waypoints by tag %s:", tag.name())
                                        : "All waypoints:"
                        ),
                        transform,
                        String.format(
                                "%s%s %s%s",
                                FORCE_COMMAND_PREFIX,
                                label,
                                action.names[0],
                                tag != null ? " " + tag.getName() : ""
                        )
                );
            } else {
                args.requireMax(0);
                throw new CommandInvalidStateException(
                        tag != null
                                ? "No waypoints found by that tag"
                                : "No waypoints found"
                );
            }
        } else if (action == Action.SAVE) {
            IWaypoint.Tag tag = IWaypoint.Tag.getByName(args.getString());
            if (tag == null) {
                throw new CommandInvalidStateException(String.format("'%s' is not a tag ", args.consumedString()));
            }
            String name = args.hasAny() ? args.getString() : "";
            BetterBlockPos pos = args.hasAny()
                    ? args.getDatatypePost(RelativeBlockPos.INSTANCE, ctx.playerFeet())
                    : ctx.playerFeet();
            args.requireMax(0);
            IWaypoint waypoint = new Waypoint(name, tag, pos);
            ForWaypoints.waypoints(this.baritone).addWaypoint(waypoint);
            ITextComponent component = new TextComponentString("Waypoint added: ");
            component.getStyle().setColor(TextFormatting.GRAY);
            component.appendSibling(toComponent.apply(waypoint, Action.INFO));
            logDirect(component);
        } else if (action == Action.CLEAR) {
            args.requireMax(1);
            IWaypoint.Tag tag = IWaypoint.Tag.getByName(args.getString());
            IWaypoint[] waypoints = ForWaypoints.getWaypointsByTag(this.baritone, tag);
            for (IWaypoint waypoint : waypoints) {
                ForWaypoints.waypoints(this.baritone).removeWaypoint(waypoint);
            }
            logDirect(String.format("Cleared %d waypoints", waypoints.length));
        } else {
            IWaypoint[] waypoints = args.getDatatypeFor(ForWaypoints.INSTANCE);
            IWaypoint waypoint = null;
            if (args.hasAny() && args.peekString().equals("@")) {
                args.requireExactly(2);
                args.get();
                long timestamp = args.getAs(Long.class);
                for (IWaypoint iWaypoint : waypoints) {
                    if (iWaypoint.getCreationTimestamp() == timestamp) {
                        waypoint = iWaypoint;
                        break;
                    }
                }
                if (waypoint == null) {
                    throw new CommandInvalidStateException("Timestamp was specified but no waypoint was found");
                }
            } else {
                switch (waypoints.length) {
                    case 0:
                        throw new CommandInvalidStateException("No waypoints found");
                    case 1:
                        waypoint = waypoints[0];
                        break;
                    default:
                        break;
                }
            }
            if (waypoint == null) {
                args.requireMax(1);
                Paginator.paginate(
                        args,
                        waypoints,
                        () -> logDirect("Multiple waypoints were found:"),
                        transform,
                        String.format(
                                "%s%s %s %s",
                                FORCE_COMMAND_PREFIX,
                                label,
                                action.names[0],
                                args.consumedString()
                        )
                );
            } else {
                if (action == Action.INFO) {
                    logDirect(transform.apply(waypoint));
                    logDirect(String.format("Position: %s", waypoint.getLocation()));
                    ITextComponent deleteComponent = new TextComponentString("Click to delete this waypoint");
                    deleteComponent.getStyle().setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            String.format(
                                    "%s%s delete %s @ %d",
                                    FORCE_COMMAND_PREFIX,
                                    label,
                                    waypoint.getTag().getName(),
                                    waypoint.getCreationTimestamp()
                            )
                    ));
                    ITextComponent goalComponent = new TextComponentString("Click to set goal to this waypoint");
                    goalComponent.getStyle().setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            String.format(
                                    "%s%s goal %s @ %d",
                                    FORCE_COMMAND_PREFIX,
                                    label,
                                    waypoint.getTag().getName(),
                                    waypoint.getCreationTimestamp()
                            )
                    ));
                    ITextComponent backComponent = new TextComponentString("Click to return to the waypoints list");
                    backComponent.getStyle().setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            String.format(
                                    "%s%s list",
                                    FORCE_COMMAND_PREFIX,
                                    label
                            )
                    ));
                    logDirect(deleteComponent);
                    logDirect(goalComponent);
                    logDirect(backComponent);
                } else if (action == Action.DELETE) {
                    ForWaypoints.waypoints(this.baritone).removeWaypoint(waypoint);
                    logDirect("That waypoint has successfully been deleted");
                } else if (action == Action.GOAL) {
                    Goal goal = new GoalBlock(waypoint.getLocation());
                    baritone.getCustomGoalProcess().setGoal(goal);
                    logDirect(String.format("Goal: %s", goal));
                }
            }
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            if (args.hasExactlyOne()) {
                return new TabCompleteHelper()
                        .append(Action.getAllNames())
                        .sortAlphabetically()
                        .filterPrefix(args.getString())
                        .stream();
            } else {
                Action action = Action.getByName(args.getString());
                if (args.hasExactlyOne()) {
                    if (action == Action.LIST || action == Action.SAVE || action == Action.CLEAR) {
                        return new TabCompleteHelper()
                                .append(IWaypoint.Tag.getAllNames())
                                .sortAlphabetically()
                                .filterPrefix(args.getString())
                                .stream();
                    } else {
                        return args.tabCompleteDatatype(ForWaypoints.INSTANCE);
                    }
                } else if (args.has(3) && action == Action.SAVE) {
                    args.get();
                    args.get();
                    return args.tabCompleteDatatype(RelativeBlockPos.INSTANCE);
                }
            }
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Manage waypoints";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "The waypoint command allows you to manage Baritone's waypoints.",
                "",
                "Waypoints can be used to mark positions for later. Waypoints are each given a tag and an optional name.",
                "",
                "Note that the info, delete, and goal commands let you specify a waypoint by tag. If there is more than one waypoint with a certain tag, then they will let you select which waypoint you mean.",
                "",
                "Usage:",
                "> wp [l/list] - List all waypoints.",
                "> wp <s/save> <tag> - Save your current position as an unnamed waypoint with the specified tag.",
                "> wp <s/save> <tag> <name> - Save the waypoint with the specified name.",
                "> wp <s/save> <tag> <name> <pos> - Save the waypoint with the specified name and position.",
                "> wp <i/info/show> <tag> - Show info on a waypoint by tag.",
                "> wp <d/delete> <tag> - Delete a waypoint by tag.",
                "> wp <g/goal/goto> <tag> - Set a goal to a waypoint by tag."
        );
    }

    private enum Action {
        LIST("list", "get", "l"),
        CLEAR("clear", "c"),
        SAVE("save", "s"),
        INFO("info", "show", "i"),
        DELETE("delete", "d"),
        GOAL("goal", "goto", "g");
        private final String[] names;

        Action(String... names) {
            this.names = names;
        }

        public static Action getByName(String name) {
            for (Action action : Action.values()) {
                for (String alias : action.names) {
                    if (alias.equalsIgnoreCase(name)) {
                        return action;
                    }
                }
            }
            return null;
        }

        public static String[] getAllNames() {
            Set<String> names = new HashSet<>();
            for (Action action : Action.values()) {
                names.addAll(Arrays.asList(action.names));
            }
            return names.toArray(new String[0]);
        }
    }
}
