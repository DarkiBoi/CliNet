package me.zeroeightsix.kami.module.modules.combat;


import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "Surround", description = "Places blocks around you", category = Module.Category.COMBAT)
public class Surround extends Module {


    private List<BlockPos> blockPos = new ArrayList<>();

    @Override
    public void onUpdate() {
        blockPos.clear();
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(1, -1, -1, -1, 0, 1);
        blocks.forEach(pos -> {
            for(Vec3d vector : VECTORS) {
                try {
                    BlockInteractionHelper.faceVectorPacketInstant(vector);
                    mc.playerController.clickBlock(pos, BlockInteractionHelper.getPlaceableSide(pos));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }



    private static final Vec3d[] VECTORS = {
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, 1),
            new Vec3d(-1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, 1),
            new Vec3d(-1, -1, 0),
            new Vec3d(0, -1, -1)
    };


}
