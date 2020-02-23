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

/*
 * Created by Darki
 */

@Module.Info(name = "HoleESP", description = "Highlights hole for bedrock and obsidian", category=  Module.Category.RENDER)
public class HoleESP extends Module {

    private Integer[] brockColour = new Integer[] {255, 0, 0, 40};
    private Integer[] obiColour = new Integer[] {0, 255, 0, 40};

    private Setting<Integer> rangeXZ = register(Settings.integerBuilder("Range XZ").withMinimum(1).withMaximum(25).withValue(15).build());
    private Setting<Integer> rangeY = register(Settings.integerBuilder("Range Y").withMinimum(1).withMaximum(25).withValue(15).build());

    public static Setting<RenderMode> renderMode = Settings.e("Render Mode", RenderMode.BOTH);

    private Setting<Integer> rBrockHoleColour = register(Settings.integerBuilder("Bedrock Red").withMinimum(0).withMaximum(255).withValue(brockColour[0]).build());
    private Setting<Integer> gBrockHoleColour = register(Settings.integerBuilder("Bedrock Green").withMinimum(0).withMaximum(255).withValue(brockColour[1]).build());
    private Setting<Integer> bBrockHoleColour = register(Settings.integerBuilder("Bedrock Blue").withMinimum(0).withMaximum(255).withValue(brockColour[2]).build());
    private Setting<Integer> aBrockHoleColour = register(Settings.integerBuilder("Bedrock Alpha").withMinimum(0).withMaximum(255).withValue(brockColour[3]).build());

    private Setting<Integer> rObiHoleColour = register(Settings.integerBuilder("Obi Red").withMinimum(0).withMaximum(255).withValue(obiColour[0]).build());
    private Setting<Integer> gObiHoleColour = register(Settings.integerBuilder("Obi Green").withMinimum(0).withMaximum(255).withValue(obiColour[1]).build());
    private Setting<Integer> bObiHoleColour = register(Settings.integerBuilder("Obi Blue").withMinimum(0).withMaximum(255).withValue(obiColour[2]).build());
    private Setting<Integer> aObiHoleColour = register(Settings.integerBuilder("Obi Alpha").withMinimum(0).withMaximum(255).withValue(obiColour[3]).build());

    public static Setting<Mode> mode = Settings.e("Mode", Mode.HALFBOX);

    private List<BlockPos> obiHoles = new ArrayList<BlockPos>();
    private List<BlockPos> bedrockHoles = new ArrayList<BlockPos>();

    private static HoleESP instance = new HoleESP();

    public HoleESP() {
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
        obiHoles.clear();
        bedrockHoles.clear();
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(mc.player.getPosition().add(-rangeXZ.getValue(), -rangeY.getValue(), -rangeXZ.getValue()), mc.player.getPosition().add(rangeXZ.getValue(), rangeY.getValue(), rangeXZ.getValue()));

        for (BlockPos pos : blocks) {
            if (!(
                    mc.world.getBlockState(pos).getMaterial().blocksMovement() &&
                    mc.world.getBlockState(pos.add(0,1,0)).getMaterial().blocksMovement() &&
                    mc.world.getBlockState(pos.add(0,2,0)).getMaterial().blocksMovement()
            )) {

                if (validObi(pos)) {
                    this.obiHoles.add(pos);
                }

                if(validBedrock(pos)) {
                    this.bedrockHoles.add(pos);
                }

            }
        }

    }

    @Override
    public void onWorldRender(RenderEvent event) {
        KamiTessellator.prepare(GL11.GL_QUADS);
        switch(renderMode.getValue()) {
            case OBSIDIAN:
                obiHoles.forEach(pos -> drawHole(pos, rObiHoleColour.getValue(), gObiHoleColour.getValue(), bObiHoleColour.getValue(), aObiHoleColour.getValue()));
                break;
            case BEDROCK:
                bedrockHoles.forEach(pos -> drawHole(pos, rBrockHoleColour.getValue(), gBrockHoleColour.getValue(), bBrockHoleColour.getValue(), aBrockHoleColour.getValue()));
                break;
            case BOTH:
                obiHoles.forEach(pos -> drawHole(pos, rObiHoleColour.getValue(), gObiHoleColour.getValue(), bObiHoleColour.getValue(), aObiHoleColour.getValue()));
                bedrockHoles.forEach(pos -> drawHole(pos, rBrockHoleColour.getValue(), gBrockHoleColour.getValue(), bBrockHoleColour.getValue(), aBrockHoleColour.getValue()));
        }

        KamiTessellator.release();
    }

    private boolean validObi(BlockPos pos) {
        if(validBedrock(pos)) {
            return false;
        }

        if (mc.world.getBlockState(pos.add(0,-1,0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.add(0,-1,0)).getBlock() == Blocks.BEDROCK) {
            if (mc.world.getBlockState(pos.add(1,0,0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.add(1,0,0)).getBlock() == Blocks.BEDROCK) {
                if (mc.world.getBlockState(pos.add(-1,0,0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.add(-1,0,0)).getBlock() == Blocks.BEDROCK) {
                    if (mc.world.getBlockState(pos.add(0,0,1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.add(0,0,1)).getBlock() == Blocks.BEDROCK) {
                        if (mc.world.getBlockState(pos.add(0,0,-1)).getBlock() == Blocks.OBSIDIAN  || mc.world.getBlockState(pos.add(0,0,-1)).getBlock() == Blocks.BEDROCK) {
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

    private boolean validBedrock(BlockPos pos) {
        if (mc.world.getBlockState(pos.add(0,-1,0)).getBlock() == Blocks.BEDROCK) {
            if (mc.world.getBlockState(pos.add(1,0,0)).getBlock() == Blocks.BEDROCK) {
                if (mc.world.getBlockState(pos.add(-1,0,0)).getBlock() == Blocks.BEDROCK) {
                    if (mc.world.getBlockState(pos.add(0,0,1)).getBlock() == Blocks.BEDROCK) {
                        if (mc.world.getBlockState(pos.add(0,0,-1)).getBlock() == Blocks.BEDROCK) {
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
