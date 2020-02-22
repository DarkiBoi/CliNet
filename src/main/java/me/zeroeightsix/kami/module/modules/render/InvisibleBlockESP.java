package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "InvisibleBlockESP", description = "Highlights invisible blocks such as string and barriers", category=  Module.Category.RENDER)
public class InvisibleBlockESP extends Module {

    private Setting<Integer> rangeXZ = register(Settings.integerBuilder("Range XZ").withMinimum(1).withMaximum(25).withValue(15).build());
    private Setting<Integer> rangeY = register(Settings.integerBuilder("Range Y").withMinimum(1).withMaximum(25).withValue(15).build());

    private Setting<Boolean> barrierSetting = register(Settings.b("Barrier", true));
    private Setting<Boolean> stringSetting = register(Settings.b("String", true));


    private List<BlockPos> barriers = new ArrayList<BlockPos>();
    private List<BlockPos> string = new ArrayList<BlockPos>();

    @Override
    public void onUpdate() {
        barriers.clear();
        string.clear();
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(mc.player.getPosition().add(-rangeXZ.getValue(), -rangeY.getValue(), -rangeXZ.getValue()), mc.player.getPosition().add(rangeXZ.getValue(), rangeY.getValue(), rangeXZ.getValue()));
        for (BlockPos pos : blocks) {
            if (barrierSetting.getValue() && mc.world.getBlockState(pos).getBlock() == Blocks.BARRIER) barriers.add(pos);
            if (stringSetting.getValue() && mc.world.getBlockState(pos).getBlock() == Blocks.TRIPWIRE) string.add(pos);
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        KamiTessellator.prepare(GL11.GL_QUADS);
        barriers.forEach(pos -> KamiTessellator.drawBox(pos, 0x22FF4437, GeometryMasks.Quad.ALL));
        string.forEach(pos -> KamiTessellator.drawHalfBox(pos, 0x22B0B0FC, GeometryMasks.Quad.ALL));
        KamiTessellator.release();
    }
}
