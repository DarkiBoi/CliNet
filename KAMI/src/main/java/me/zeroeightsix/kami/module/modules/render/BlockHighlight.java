package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;


@Module.Info(name = "BlockHighlight", category = Module.Category.RENDER, description = "Better crosshair highlighting")
public class BlockHighlight extends Module {


    @Override
    public void onUpdate(){
        try{
        RayTraceResult result = mc.objectMouseOver;
        if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            Command.sendChatMessage(pos.toString());
        }
            } catch (Exception e) {
            return;
        }
    }

}


