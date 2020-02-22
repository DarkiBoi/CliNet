package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "VoidESP", description = "Highlights Void Holes", category = Module.Category.RENDER)
public class VoidESP extends Module {

    private Integer[] voidColour = new Integer[] {255, 0, 255, 40};

    private Setting<Integer> rangeXZ = register(Settings.integerBuilder("Range XZ").withMinimum(1).withMaximum(25).withValue(15).build());
    private Setting<Integer> rangeY = register(Settings.integerBuilder("Range Y").withMinimum(1).withMaximum(25).withValue(15).build());

    public static Setting<RenderMode> renderMode = Settings.e("Render Mode", RenderMode.BOTH);

    private Setting<Integer> rVoidColour = register(Settings.integerBuilder("Red").withMinimum(0).withMaximum(255).withValue(voidColour[0]).build());
    private Setting<Integer> gVoidColour = register(Settings.integerBuilder("Green").withMinimum(0).withMaximum(255).withValue(voidColour[1]).build());
    private Setting<Integer> bVoidColour = register(Settings.integerBuilder("Blue").withMinimum(0).withMaximum(255).withValue(voidColour[2]).build());
    private Setting<Integer> aVoidColour = register(Settings.integerBuilder("Alpha").withMinimum(0).withMaximum(255).withValue(voidColour[3]).build());

    public static Setting<Mode> mode = Settings.e("Mode", Mode.HALFBOX);

    private List<BlockPos> voidHoles = new ArrayList<BlockPos>();

    private static VoidESP instance = new VoidESP();

    public VoidESP() {
        instance = this;
        register(renderMode);
        register(mode);
    }

    public enum RenderMode {
        OBSIDIAN, BEDROCK, BOTH
    }

    public enum Mode {
        BOX, HALFBOX, PLANE
    }

    @Override
    public void onUpdate() {
        voidHoles.clear();
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(mc.player.getPosition().add(-rangeXZ.getValue(), -rangeY.getValue(), -rangeXZ.getValue()), mc.player.getPosition().add(rangeXZ.getValue(), rangeY.getValue(), rangeXZ.getValue()));

        for (BlockPos pos : blocks) {
            if (!(
                    mc.world.getBlockState(pos).getMaterial().blocksMovement() &&
                            mc.world.getBlockState(pos.add(0,1,0)).getMaterial().blocksMovement() &&
                            mc.world.getBlockState(pos.add(0,2,0)).getMaterial().blocksMovement()
            )) {

                if (validVoid(pos)) {
                    this.voidHoles.add(pos);
                }

            }
        }

    }

    @Override
    public void onWorldRender(RenderEvent event) {
        KamiTessellator.prepare(GL11.GL_QUADS);
        voidHoles.forEach(pos -> drawHole(pos, rVoidColour.getValue(), gVoidColour.getValue(), bVoidColour.getValue(), aVoidColour.getValue()));
        KamiTessellator.release();
    }

    private boolean validVoid(BlockPos pos) {
        if(isZeroBlockPos(pos) && mc.world.getBlockState(pos).getBlock() == Blocks.AIR) return true;
        return false;
    }

    public boolean isZeroBlockPos(BlockPos pos) {
        if(pos.y == 0) return true;
        return false;
    }

    public void drawHole(BlockPos pos, int r, int g, int b, int a) {
        Color color = new Color(r, g, b, a);
        switch (mode.getValue()) {
            case BOX:
                KamiTessellator.drawBox(pos, color.getRGB(), GeometryMasks.Quad.ALL);
                break;
            case HALFBOX:
                KamiTessellator.drawHalfBox(pos, color.getRGB(), GeometryMasks.Quad.ALL);
                break;
            case PLANE:
                KamiTessellator.drawPlane(pos, color.getRGB(), GeometryMasks.Quad.ALL);
        }
    }
}
