package me.zeroeightsix.kami.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
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
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Module.Info(name = "Surround", description = "Places blocks around you!", category = Module.Category.COMBAT)
public class Surround extends Module {

    private Setting<Mode> mode = register(Settings.e("Mode", Mode.FULL));
    private Setting<Boolean> triggerable = register(Settings.b("Triggerable", true));
    private Setting<Integer> timeoutTicks = register(Settings.integerBuilder("TimeoutTicks").withMinimum(1).withValue(40).withMaximum(100).withVisibility(b -> triggerable.getValue()).build());
    private Setting<Integer> blocksPerTick = register(Settings.integerBuilder("BlocksPerTick").withMinimum(1).withValue(4).withMaximum(9).build());
    private Setting<Integer> tickDelay = register(Settings.integerBuilder("TickDelay").withMinimum(0).withValue(0).withMaximum(10).build());
    private Setting<Boolean> rotate = register(Settings.b("Rotate", true));
    private Setting<Boolean> noGlitchBlocks = register(Settings.b("NoGlitchBlocks", true));
    private Setting<Boolean> announceUsage = register(Settings.b("AnnounceUsage", true));
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private int offsetStep = 0;
    private int delayStep = 0;
    private int totalTicksRunning = 0;
    private boolean firstRun;
    private boolean isSneaking = false;

    @Override
    protected void onEnable() {

        if (mc.player == null) {
            this.disable();
            return;
        }

        firstRun = true;

        // save initial player hand
        playerHotbarSlot = mc.player.inventory.currentItem;
        lastHotbarSlot = -1;

        if (announceUsage.getValue()) {
            Command.sendChatMessage("[AutoFeetPlace] " + ChatFormatting.GREEN.toString() + "Enabled!");
        }

    }

    @Override
    protected void onDisable() {

        if (mc.player == null) {
            return;
        }

        // load initial player hand
        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            mc.player.inventory.currentItem = playerHotbarSlot;
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        playerHotbarSlot = -1;
        lastHotbarSlot = -1;

        if (announceUsage.getValue()) {
            Command.sendChatMessage("[AutoFeetPlace] " + ChatFormatting.RED.toString() + "Disabled!");
        }

    }

    @Override
    public void onUpdate() {

        if (mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }

        if (triggerable.getValue() && totalTicksRunning >= timeoutTicks.getValue()) {
            totalTicksRunning = 0;
            this.disable();
            return;
        }

        if (!firstRun) {
            if (delayStep < tickDelay.getValue()) {
                delayStep++;
                return;
            } else {
                delayStep = 0;
            }
        }

        if (firstRun) {
            firstRun = false;
        }

        int blocksPlaced = 0;

        while (blocksPlaced < blocksPerTick.getValue()) {

            Vec3d[] offsetPattern = new Vec3d[0];
            int maxSteps = 0;

            if (mode.getValue().equals(Mode.FULL)) {
                offsetPattern = Offsets.FULL;
                maxSteps = Offsets.FULL.length;
            }

            if (mode.getValue().equals(Mode.SURROUND)) {
                offsetPattern = Offsets.SURROUND;
                maxSteps = Offsets.SURROUND.length;
            }

            if (offsetStep >= maxSteps) {
                offsetStep = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(offsetPattern[offsetStep]);
            BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.x, offsetPos.y, offsetPos.z);

            if (placeBlock(targetPos)) {
                blocksPlaced++;
            }

            offsetStep++;

        }

        if (blocksPlaced > 0) {

            if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                mc.player.inventory.currentItem = playerHotbarSlot;
                lastHotbarSlot = playerHotbarSlot;
            }

            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }

        }

        totalTicksRunning++;

    }

    private boolean placeBlock(BlockPos pos) {

        // check if block is already placed
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        // check if entity blocks placing
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return false;
            }
        }

        EnumFacing side = BlockInteractionHelper.getPlaceableSide(pos);

        // check if we have a block adjacent to blockpos to click at
        if (side == null) {
            return false;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        // check if neighbor can be right clicked
        if (!BlockInteractionHelper.canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        int obiSlot = findObiInHotbar();

        if (obiSlot == -1) {
            this.disable();
        }

        if (lastHotbarSlot != obiSlot) {
            mc.player.inventory.currentItem = obiSlot;
            lastHotbarSlot = obiSlot;
        }

        if (!isSneaking && BlockInteractionHelper.blackList.contains(neighbourBlock) || BlockInteractionHelper.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        if (rotate.getValue()) {
            BlockInteractionHelper.faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;

        if (noGlitchBlocks.getValue() && !mc.playerController.getCurrentGameType().equals(GameType.CREATIVE)) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, neighbour, opposite));
        }

        return true;

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

    private enum Mode {
        SURROUND, FULL
    }

    private static class Offsets {

        private static final Vec3d[] SURROUND = {
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, -1)
        };

        private static final Vec3d[] FULL = {
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, -1),
                new Vec3d(0, -1, 0)
        };

    }

}
