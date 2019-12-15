package me.zeroeightsix.kami.gui.kami.module.modules.player;

import me.zeroeightsix.kami.gui.kami.module.Module;

/**
 * @author 086
 */
@Module.Info(name = "Fastbreak", category = Module.Category.PLAYER, description = "Nullifies block hit delay")
public class Fastbreak extends Module {

    @Override
    public void onUpdate() {
        mc.playerController.blockHitDelay = 0;
    }
}
