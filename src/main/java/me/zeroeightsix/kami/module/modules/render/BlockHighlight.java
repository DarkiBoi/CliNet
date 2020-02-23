package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;


/*
 * Created by Darki / Hamburger
 */

@Module.Info(name = "BlockHighlight", category = Module.Category.RENDER, description = "Better crosshair highlighting")
public class BlockHighlight extends Module {

    private Integer[] colourArray = new Integer[] {255, 0, 0, 40};
    private Setting<Integer> rSetting = register(Settings.integerBuilder("Red").withMinimum(0).withMaximum(255).withValue(colourArray[0]).build());
    private Setting<Integer> gSetting = register(Settings.integerBuilder("Green").withMinimum(0).withMaximum(255).withValue(colourArray[1]).build());
    private Setting<Integer> bSetting = register(Settings.integerBuilder("Blue").withMinimum(0).withMaximum(255).withValue(colourArray[2]).build());
    private Setting<Integer> aSetting = register(Settings.integerBuilder("Alpha").withMinimum(0).withMaximum(255).withValue(colourArray[3]).build());
    RayTraceResult result;

    @Override
    public void onUpdate() {
        if(mc.world == null || mc.player == null) {
            return;
        }
        result = mc.objectMouseOver;
        if(result == null) {
            return;
        }

    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if(mc.world == null || mc.player == null) {
            return;
        }
        if(result == null) {
            return;
        }

        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            final BlockPos pos = result.getBlockPos();
            final IBlockState blockState = mc.world.getBlockState(pos);

            KamiTessellator.prepare(GL11.GL_QUADS);
            KamiTessellator.drawBox(pos, rSetting.getValue(), gSetting.getValue(), bSetting.getValue(), aSetting.getValue(), GeometryMasks.Line.ALL);
            KamiTessellator.release();

        }


    }

}


