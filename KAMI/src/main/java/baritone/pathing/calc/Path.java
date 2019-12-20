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

package baritone.pathing.calc;

import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.movement.IMovement;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.Helper;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.Moves;
import baritone.pathing.path.CutoffPath;
import baritone.utils.pathing.PathBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A node based implementation of IPath
 *
 * @author leijurv
 */
class Path extends PathBase {

    /**
     * The start position of this path
     */
    private final BetterBlockPos start;

    /**
     * The end position of this path
     */
    private final BetterBlockPos end;

    /**
     * The blocks on the path. Guaranteed that path.get(0) equals start and
     * path.get(path.size()-1) equals end
     */
    private final List<BetterBlockPos> path;

    private final List<Movement> movements;

    private final List<PathNode> nodes;

    private final Goal goal;

    private final int numNodes;

    private final CalculationContext context;

    private volatile boolean verified;

    Path(PathNode start, PathNode end, int numNodes, Goal goal, CalculationContext context) {
        this.start = new BetterBlockPos(start.x, start.y, start.z);
        this.end = new BetterBlockPos(end.x, end.y, end.z);
        this.numNodes = numNodes;
        this.movements = new ArrayList<>();
        this.goal = goal;
        this.context = context;
        PathNode current = end;
        LinkedList<BetterBlockPos> tempPath = new LinkedList<>();
        LinkedList<PathNode> tempNodes = new LinkedList<>();
        // Repeatedly inserting to the beginning of an arraylist is O(n^2)
        // Instead, do it into a linked list, then convert at the end
        while (current != null) {
            tempNodes.addFirst(current);
            tempPath.addFirst(new BetterBlockPos(current.x, current.y, current.z));
            current = current.previous;
        }
        // Can't directly convert from the PathNode pseudo linked list to an array because we don't know how long it is
        // inserting into a LinkedList<E> keeps track of length, then when we addall (which calls .toArray) it's able
        // to performantly do that conversion since it knows the length.
        this.path = new ArrayList<>(tempPath);
        this.nodes = new ArrayList<>(tempNodes);
    }

    @Override
    public Goal getGoal() {
        return goal;
    }

    private boolean assembleMovements() {
        if (path.isEmpty() || !movements.isEmpty()) {
            throw new IllegalStateException();
        }
        for (int i = 0; i < path.size() - 1; i++) {
            double cost = nodes.get(i + 1).cost - nodes.get(i).cost;
            Movement move = runBackwards(path.get(i), path.get(i + 1), cost);
            if (move == null) {
                return true;
            } else {
                movements.add(move);
            }
        }
        return false;
    }

    private Movement runBackwards(BetterBlockPos src, BetterBlockPos dest, double cost) {
        for (Moves moves : Moves.values()) {
            Movement move = moves.apply0(context, src);
            if (move.getDest().equals(dest)) {
                // have to calculate the cost at calculation time so we can accurately judge whether a cost increase happened between cached calculation and real execution
                // however, taking into account possible favoring that could skew the node cost, we really want the stricter limit of the two
                // so we take the minimum of the path node cost difference, and the calculated cost
                move.override(Math.min(move.calculateCost(context), cost));
                return move;
            }
        }
        // this is no longer called from bestPathSoFar, now it's in postprocessing
        Helper.HELPER.logDebug("Movement became impossible during calculation " + src + " " + dest + " " + dest.subtract(src));
        return null;
    }

    @Override
    public IPath postProcess() {
        if (verified) {
            throw new IllegalStateException();
        }
        verified = true;
        boolean failed = assembleMovements();
        movements.forEach(m -> m.checkLoadedChunk(context));

        if (failed) { // at least one movement became impossible during calculation
            CutoffPath res = new CutoffPath(this, movements().size());
            if (res.movements().size() != movements.size()) {
                throw new IllegalStateException();
            }
            return res;
        }
        // more post processing here
        sanityCheck();
        return this;
    }

    @Override
    public List<IMovement> movements() {
        if (!verified) {
            throw new IllegalStateException();
        }
        return Collections.unmodifiableList(movements);
    }

    @Override
    public List<BetterBlockPos> positions() {
        return Collections.unmodifiableList(path);
    }

    @Override
    public int getNumNodesConsidered() {
        return numNodes;
    }

    @Override
    public BetterBlockPos getSrc() {
        return start;
    }

    @Override
    public BetterBlockPos getDest() {
        return end;
    }
}
