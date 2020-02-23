package me.zeroeightsix.kami.module.modules.player;

import me.zeroeightsix.kami.module.Module;

/*
 * Created by Darki
 */

@Module.Info(name = "InvalidTeleport", category = Module.Category.MISC)
public class InvalidTeleport extends Module {


    double randomX = Math.random() * 1000 + 1;
    double randomY = Math.random() * 1000 + 1;
    double randomZ = Math.random() * 1000 + 1;



    @Override
    public void onEnable() {
        if(mc.player == null || mc.world == null) {
            return;
        }
        /*
        if (mc.isSingleplayer()) {
            Command.sendChatMessage("Cant use in Singleplayer!");
            return;
        }*/

        mc.player.posX += randomX;
        mc.player.posZ += randomZ;
        mc.player.posY += randomY;

        this.setEnabled(false);

        mc.renderGlobal.loadRenderers();

    }

}
