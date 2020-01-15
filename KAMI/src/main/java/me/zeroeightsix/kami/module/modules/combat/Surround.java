package me.zeroeightsix.kami.module.modules.combat;


import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.EntityUtil;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "Surround", description = "Places blocks around you", category = Module.Category.COMBAT)
public class Surround extends Module {


    private List<BlockPos> blockPos = new ArrayList<>();
    private Setting<Boolean> disableSetting = register(Settings.b("Disable", false));
    private Setting<Integer> delay = register(Settings.integerBuilder().withName("Delay").withMinimum(1).withMaximum(20).withValue(5).build());


    private static float originalYaw;
    private static float originalPitch;

    @Override
    public void onUpdate() {
        originalYaw = mc.player.rotationYaw;
        originalPitch = mc.player.rotationPitch;
        for (Vec3d vec : VECTORS) {
            double[] lookAt = EntityUtil.calculateLookAt(mc.player.posX + vec.x, mc.player.posY + vec.y, mc.player.posZ + vec.z, mc.player);
            if(validBlock(new BlockPos(mc.player.posX + vec.x, mc.player.posY + vec.y, mc.player.posZ + vec.z))) continue;
            mc.player.rotationYaw = (float) lookAt[0];
            mc.player.rotationPitch = (float) lookAt[1];
            mc.player.connection.sendPacket(
                    new CPacketPlayerTryUseItemOnBlock(
                            new BlockPos(mc.player.posX + vec.x, mc.player.posY + vec.y, mc.player.posZ + vec.z),
                            EnumFacing.UP,
                            EnumHand.MAIN_HAND,
                            0,
                            0,
                            0
                    )
            );


        }

        mc.player.rotationYaw = originalYaw;
        mc.player.rotationPitch = originalPitch;

        if(disableSetting.getValue()) {
            this.setEnabled(false);
            return;
        }

    }

    public boolean validBlock(BlockPos pos) {
        if(mc.world.getBlockState(pos).getBlock().isFullBlock(mc.world.getBlockState(pos))) {
            return true;
        }

        return false;

    }



    private static final Vec3d[] VECTORS = {
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, 1),
            new Vec3d(-1, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, 1),
            new Vec3d(-1, 0, 0),
            new Vec3d(0, 0, -1)
    };


}
