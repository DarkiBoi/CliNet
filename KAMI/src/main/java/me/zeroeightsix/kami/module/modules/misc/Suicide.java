package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;


/*
* Created by Hamburger 8/1/2020
 */

@Module.Info(name = "Suicide (KILLS YOU!)", category = Module.Category.MISC, description = "Kills self by running the /kill command")
public class Suicide extends Module {

    @Override
    public void onEnable() {
        mc.player.sendChatMessage("/kill");
        this.disable();
        }



}
