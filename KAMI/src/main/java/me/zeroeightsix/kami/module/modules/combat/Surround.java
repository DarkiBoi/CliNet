package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import me.zeroeightsix.kami.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Module.Info(name = "Surround", description = "Places blocks around you", category = Module.Category.COMBAT)
public class Surround extends Module {

    private static final Vec3d[] VECTORS = {
            new Vec3d(0, -1, 0),
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, 1),
            new Vec3d(-1, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, 1),
            new Vec3d(-1, 0, 0),
            new Vec3d(0, 0, -1)
    };

    private Setting<Boolean> disableSetting = register(Settings.b("Disable", false));
    private Setting<Boolean> rotateSetting = register(Settings.b("Rotate", true));
    private Setting<Integer> delaySetting = register(Settings.integerBuilder().withName("Delay").withMinimum(0).withMaximum(5).withValue(1).build());
    private Setting<Boolean> debugSetting = register(Settings.b("Debug", false));

    private int delayTicksCurrent = 0;

    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;

    @Override
    public void onEnable() {
        playerHotbarSlot = mc.player.inventory.currentItem;
        lastHotbarSlot = -1;
    }

    @Override
    public void onDisable() {
        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            mc.player.inventory.currentItem = playerHotbarSlot;
        }

        playerHotbarSlot = -1;
        lastHotbarSlot = -1;


    }

    @Override
    public void onUpdate() {

        float originalYaw = mc.player.rotationYaw;
        float originalPitch = mc.player.rotationPitch;

        boolean allBlocksPlaced = false;

        if (delayTicksCurrent >= delaySetting.getValue()) {

            for (Vec3d vec : VECTORS) {

                allBlocksPlaced = true;

                BlockPos placeTarget = new BlockPos(mc.player.posX + vec.x, mc.player.getPosition().y + vec.y, mc.player.posZ + vec.z);

                // skip already placed blocks
                if (isPlacingBlocked(placeTarget)) {
                    continue;
                }

                allBlocksPlaced = false;

                double[] lookAt = EntityUtil.calculateLookAt(mc.player.posX + vec.x, mc.player.posY + vec.y, mc.player.posZ + vec.z, mc.player);
                mc.player.rotationYaw = (float) lookAt[0];
                mc.player.rotationPitch = (float) lookAt[1];

                /*mc.player.connection.sendPacket(
                        new CPacketPlayerTryUseItemOnBlock(
                                placeTarget,
                                EnumFacing.UP,
                                EnumHand.MAIN_HAND,
                                0,
                                0,
                                0
                        )
                );*/

                EnumFacing side = BlockInteractionHelper.getPlaceableSide(placeTarget);

                // check if we have a block adjacent to blockpos to click at
                if (side == null) {
                    return;
                }

                BlockPos neighbour = placeTarget.offset(side);
                EnumFacing opposite = side.getOpposite();

                // check if neighbor can be right clicked
                if (!BlockInteractionHelper.canBeClicked(neighbour)) {
                    return;
                }

                Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
                Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

                int obiSlot = findObiInHotbar();

                if (lastHotbarSlot != obiSlot) {
                    mc.player.inventory.currentItem = obiSlot;
                    lastHotbarSlot = obiSlot;
                }

                if (!mc.player.isSneaking() && BlockInteractionHelper.blackList.contains(neighbourBlock) || BlockInteractionHelper.shulkerList.contains(neighbourBlock)) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    mc.player.setSneaking(true);
                }

                if (rotateSetting.getValue()) {
                    BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                }

                mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.rightClickDelayTimer = 4;

                if(debugSetting.getValue()) {
                    Command.sendChatMessage("Placing: " + vec.x + " " + vec.y + " " + " " + vec.z + " at: " + placeTarget.x + " " + placeTarget.y + " " + placeTarget.z);
                }


                if (delaySetting.getValue() > 0) {
                    break;
                }

            }

            delayTicksCurrent = 0;

        } else {
            delayTicksCurrent++;
        }

        mc.player.rotationYaw = originalYaw;
        mc.player.rotationPitch = originalPitch;

        if (allBlocksPlaced && disableSetting.getValue()) {
            Command.sendChatMessage("All blocks placed, disabling!");
            this.disable();
        }

    }

    private boolean isPlacingBlocked(BlockPos pos) {

        // check if block is already placed except air and liquid
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return true;
        }

        // check if entity blocks placing except items and xp orbs
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return true;
            }
        }

        return false;

    }

    private int findObiInHotbar() {

        // search blocks in hotbar
        int slot = -1;
        for (int i = 0; i < 9; i++) {

            // filter out non-block items
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockObsidian) {
                slot = i;
                break;
            }

        }

        return slot;

    }

}
