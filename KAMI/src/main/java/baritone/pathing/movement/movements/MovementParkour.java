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

package baritone.pathing.movement.movements;

import baritone.api.IBaritone;
import baritone.api.pathing.movement.MovementStatus;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.input.Input;
import baritone.pathing.movement.CalculationContext;
import baritone.pathing.movement.Movement;
import baritone.pathing.movement.MovementHelper;
import baritone.pathing.movement.MovementState;
import baritone.utils.BlockStateInterface;
import baritone.utils.pathing.MutableMoveResult;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import java.util.HashSet;
import java.util.Set;

public class MovementParkour extends Movement {

    private static final BetterBlockPos[] EMPTY = new BetterBlockPos[]{};

    private final EnumFacing direction;
    private final int dist;
    private final boolean ascend;

    private MovementParkour(IBaritone baritone, BetterBlockPos src, int dist, EnumFacing dir, boolean ascend) {
        super(baritone, src, src.offset(dir, dist).up(ascend ? 1 : 0), EMPTY, src.offset(dir, dist).down(ascend ? 0 : 1));
        this.direction = dir;
        this.dist = dist;
        this.ascend = ascend;
    }

    public static MovementParkour cost(CalculationContext context, BetterBlockPos src, EnumFacing direction) {
        MutableMoveResult res = new MutableMoveResult();
        cost(context, src.x, src.y, src.z, direction, res);
        int dist = Math.abs(res.x - src.x) + Math.abs(res.z - src.z);
        return new MovementParkour(context.getBaritone(), src, dist, direction, res.y > src.y);
    }

    public static void cost(CalculationContext context, int x, int y, int z, EnumFacing dir, MutableMoveResult res) {
        if (!context.allowParkour) {
            return;
        }
        if (y == 256 && !context.allowJumpAt256) {
            return;
        }

        int xDiff = dir.getXOffset();
        int zDiff = dir.getZOffset();
        if (!MovementHelper.fullyPassable(context, x + xDiff, y, z + zDiff)) {
            // most common case at the top -- the adjacent block isn't air
            return;
        }
        IBlockState adj = context.get(x + xDiff, y - 1, z + zDiff);
        if (MovementHelper.canWalkOn(context.bsi, x + xDiff, y - 1, z + zDiff, adj)) { // don't parkour if we could just traverse (for now)
            // second most common case -- we could just traverse not parkour
            return;
        }
        if (MovementHelper.avoidWalkingInto(adj.getBlock()) && adj.getBlock() != Blocks.WATER && adj.getBlock() != Blocks.FLOWING_WATER) { // magma sucks
            return;
        }
        if (!MovementHelper.fullyPassable(context, x + xDiff, y + 1, z + zDiff)) {
            return;
        }
        if (!MovementHelper.fullyPassable(context, x + xDiff, y + 2, z + zDiff)) {
            return;
        }
        if (!MovementHelper.fullyPassable(context, x, y + 2, z)) {
            return;
        }
        IBlockState standingOn = context.get(x, y - 1, z);
        if (standingOn.getBlock() == Blocks.VINE || standingOn.getBlock() == Blocks.LADDER || standingOn.getBlock() instanceof BlockStairs || MovementHelper.isBottomSlab(standingOn) || standingOn.getBlock() instanceof BlockLiquid) {
            return;
        }
        int maxJump;
        if (standingOn.getBlock() == Blocks.SOUL_SAND) {
            maxJump = 2; // 1 block gap
        } else {
            if (context.canSprint) {
                maxJump = 4;
            } else {
                maxJump = 3;
            }
        }
        for (int i = 2; i <= maxJump; i++) {
            int destX = x + xDiff * i;
            int destZ = z + zDiff * i;
            if (!MovementHelper.fullyPassable(context, destX, y + 1, destZ)) {
                return;
            }
            if (!MovementHelper.fullyPassable(context, destX, y + 2, destZ)) {
                return;
            }
            IBlockState destInto = context.bsi.get0(destX, y, destZ);
            if (!MovementHelper.fullyPassable(context.bsi.access, context.bsi.isPassableBlockPos.setPos(destX, y, destZ), destInto)) {
                if (i <= 3 && context.allowParkourAscend && context.canSprint && MovementHelper.canWalkOn(context.bsi, destX, y, destZ, destInto) && checkOvershootSafety(context.bsi, destX + xDiff, y + 1, destZ + zDiff)) {
                    res.x = destX;
                    res.y = y + 1;
                    res.z = destZ;
                    res.cost = i * SPRINT_ONE_BLOCK_COST + context.jumpPenalty;
                }
                return;
            }
            IBlockState landingOn = context.bsi.get0(destX, y - 1, destZ);
            // farmland needs to be canwalkon otherwise farm can never work at all, but we want to specifically disallow ending a jumy on farmland haha
            if (landingOn.getBlock() != Blocks.FARMLAND && MovementHelper.canWalkOn(context.bsi, destX, y - 1, destZ, landingOn)) {
                if (checkOvershootSafety(context.bsi, destX + xDiff, y, destZ + zDiff)) {
                    res.x = destX;
                    res.y = y;
                    res.z = destZ;
                    res.cost = costFromJumpDistance(i) + context.jumpPenalty;
                }
                return;
            }
            if (!MovementHelper.fullyPassable(context, destX, y + 3, destZ)) {
                return;
            }
        }
        if (maxJump != 4) {
            return;
        }
        if (!context.allowParkourPlace) {
            return;
        }
        // time 2 pop off with that dank skynet parkour place
        int destX = x + 4 * xDiff;
        int destZ = z + 4 * zDiff;
        IBlockState toReplace = context.get(destX, y - 1, destZ);
        double placeCost = context.costOfPlacingAt(destX, y - 1, destZ, toReplace);
        if (placeCost >= COST_INF) {
            return;
        }
        if (!MovementHelper.isReplaceable(destX, y - 1, destZ, toReplace, context.bsi)) {
            return;
        }
        if (!checkOvershootSafety(context.bsi, destX + xDiff, y, destZ + zDiff)) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            int againstX = destX + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[i].getXOffset();
            int againstY = y - 1 + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[i].getYOffset();
            int againstZ = destZ + HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[i].getZOffset();
            if (againstX == x + xDiff * 3 && againstZ == z + zDiff * 3) { // we can't turn around that fast
                continue;
            }
            if (MovementHelper.canPlaceAgainst(context.bsi, againstX, againstY, againstZ)) {
                res.x = destX;
                res.y = y;
                res.z = destZ;
                res.cost = costFromJumpDistance(4) + placeCost + context.jumpPenalty;
                return;
            }
        }
    }

    private static boolean checkOvershootSafety(BlockStateInterface bsi, int x, int y, int z) {
        // we're going to walk into these two blocks after the landing of the parkour anyway, so make sure they aren't avoidWalkingInto
        return !MovementHelper.avoidWalkingInto(bsi.get0(x, y, z).getBlock()) && !MovementHelper.avoidWalkingInto(bsi.get0(x, y + 1, z).getBlock());
    }

    private static double costFromJumpDistance(int dist) {
        switch (dist) {
            case 2:
                return WALK_ONE_BLOCK_COST * 2; // IDK LOL
            case 3:
                return WALK_ONE_BLOCK_COST * 3;
            case 4:
                return SPRINT_ONE_BLOCK_COST * 4;
            default:
                throw new IllegalStateException("LOL " + dist);
        }
    }


    @Override
    public double calculateCost(CalculationContext context) {
        MutableMoveResult res = new MutableMoveResult();
        cost(context, src.x, src.y, src.z, direction, res);
        if (res.x != dest.x || res.y != dest.y || res.z != dest.z) {
            return COST_INF;
        }
        return res.cost;
    }

    @Override
    protected Set<BetterBlockPos> calculateValidPositions() {
        Set<BetterBlockPos> set = new HashSet<>();
        for (int i = 0; i <= dist; i++) {
            for (int y = 0; y < 2; y++) {
                set.add(src.offset(direction, i).up(y));
            }
        }
        return set;
    }

    @Override
    public boolean safeToCancel(MovementState state) {
        // once this movement is instantiated, the state is default to PREPPING
        // but once it's ticked for the first time it changes to RUNNING
        // since we don't really know anything about momentum, it suffices to say Parkour can only be canceled on the 0th tick
        return state.getStatus() != MovementStatus.RUNNING;
    }

    @Override
    public MovementState updateState(MovementState state) {
        super.updateState(state);
        if (state.getStatus() != MovementStatus.RUNNING) {
            return state;
        }
        if (ctx.playerFeet().y < src.y) {
            // we have fallen
            logDebug("sorry");
            return state.setStatus(MovementStatus.UNREACHABLE);
        }
        if (dist >= 4 || ascend) {
            state.setInput(Input.SPRINT, true);
        }
        MovementHelper.moveTowards(ctx, state, dest);
        if (ctx.playerFeet().equals(dest)) {
            Block d = BlockStateInterface.getBlock(ctx, dest);
            if (d == Blocks.VINE || d == Blocks.LADDER) {
                // it physically hurt me to add support for parkour jumping onto a vine
                // but i did it anyway
                return state.setStatus(MovementStatus.SUCCESS);
            }
            if (ctx.player().posY - ctx.playerFeet().getY() < 0.094) { // lilypads
                state.setStatus(MovementStatus.SUCCESS);
            }
        } else if (!ctx.playerFeet().equals(src)) {
            if (ctx.playerFeet().equals(src.offset(direction)) || ctx.player().posY - src.y > 0.0001) {
                if (!MovementHelper.canWalkOn(ctx, dest.down()) && !ctx.player().onGround && MovementHelper.attemptToPlaceABlock(state, baritone, dest.down(), true) == PlaceResult.READY_TO_PLACE) {
                    // go in the opposite order to check DOWN before all horizontals -- down is preferable because you don't have to look to the side while in midair, which could mess up the trajectory
                    state.setInput(Input.CLICK_RIGHT, true);
                }
                // prevent jumping too late by checking for ascend
                if (dist == 3 && !ascend) { // this is a 2 block gap, dest = src + direction * 3
                    double xDiff = (src.x + 0.5) - ctx.player().posX;
                    double zDiff = (src.z + 0.5) - ctx.player().posZ;
                    double distFromStart = Math.max(Math.abs(xDiff), Math.abs(zDiff));
                    if (distFromStart < 0.7) {
                        return state;
                    }
                }

                state.setInput(Input.JUMP, true);
            } else if (!ctx.playerFeet().equals(dest.offset(direction, -1))) {
                state.setInput(Input.SPRINT, false);
                if (ctx.playerFeet().equals(src.offset(direction, -1))) {
                    MovementHelper.moveTowards(ctx, state, src);
                } else {
                    MovementHelper.moveTowards(ctx, state, src.offset(direction, -1));
                }
            }
        }
        return state;
    }
}
