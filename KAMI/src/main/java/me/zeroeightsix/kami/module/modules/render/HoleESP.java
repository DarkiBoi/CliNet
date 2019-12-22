package me.zeroeightsix.kami.module.modules.render;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.modules.misc.ChatAppend;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import scala.tools.reflect.quasiquotes.Holes;

import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "HoleESP", description = "Highlights hole for bedrock and obsidian", category=  Module.Category.RENDER)
public class HoleESP extends Module {

    private Setting<Integer> rangeXZ = register(Settings.integerBuilder("Range XZ").withMinimum(1).withMaximum(25).withValue(15).build());
    private Setting<Integer> rangeY = register(Settings.integerBuilder("Range Y").withMinimum(1).withMaximum(25).withValue(15).build());
    public static Setting<HoleESP.Mode> mode = Settings.e("Mode", Mode.HALFBOX);

    private List<BlockPos> holes = new ArrayList<BlockPos>();

    private static HoleESP instance = new HoleESP();

    public HoleESP() {
        instance = this;
        register(mode);
    }

    public enum Mode {
        BOX, HALFBOX
    }

    @Override
    public void onUpdate() {
        holes.clear();
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(mc.player.getPosition().add(-rangeXZ.getValue(), -rangeY.getValue(), -rangeXZ.getValue()), mc.player.getPosition().add(rangeXZ.getValue(), rangeY.getValue(), rangeXZ.getValue()));

        for (BlockPos pos : blocks) {
            if (!(
                    mc.world.getBlockState(pos).getMaterial().blocksMovement() &&
                    mc.world.getBlockState(pos.add(0,1,0)).getMaterial().blocksMovement() &&
                    mc.world.getBlockState(pos.add(0,2,0)).getMaterial().blocksMovement()
            )) {

                if (valid(pos)) {
                    this.holes.add(pos);
                }

            }
        }

    }

    @Override
    public void onWorldRender(RenderEvent event) {
        KamiTessellator.prepare(GL11.GL_QUADS);
        switch (mode.getValue()) {
            case BOX:
                holes.forEach(pos -> KamiTessellator.drawBox(pos, 0x22FFFFFF, GeometryMasks.Quad.ALL));
                break;
            case HALFBOX:
                holes.forEach(pos -> KamiTessellator.drawHalfBox(pos, 0x22FFFFFF, GeometryMasks.Quad.ALL));
                break;
        }

        KamiTessellator.release();
    }

    private boolean valid(BlockPos pos) {
        if (mc.world.getBlockState(pos.add(0,-1,0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.add(0,-1,0)).getBlock() == Blocks.OBSIDIAN) {
            if (mc.world.getBlockState(pos.add(1,0,0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.add(1,0,0)).getBlock() == Blocks.OBSIDIAN) {
                if (mc.world.getBlockState(pos.add(-1,0,0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.add(-1,0,0)).getBlock() == Blocks.OBSIDIAN) {
                    if (mc.world.getBlockState(pos.add(0,0,1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.add(0,0,1)).getBlock() == Blocks.OBSIDIAN) {
                        if (mc.world.getBlockState(pos.add(0,0,-1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.add(0,0,-1)).getBlock() == Blocks.OBSIDIAN) {
                            if (mc.world.getBlockState(pos).getMaterial() == Material.AIR) {
                                if (mc.world.getBlockState(pos.add(0,1,0)).getMaterial() == Material.AIR) {
                                    if (mc.world.getBlockState(pos.add(0,2,0)).getMaterial() == Material.AIR) {
                                        return true;
                                    } return false;
                                } return false;
                            } return false;
                        } return false;
                    } return false;
                } return false;
            } return false;
        } return false;
    }
}
