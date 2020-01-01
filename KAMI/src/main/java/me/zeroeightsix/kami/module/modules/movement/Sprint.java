package me.zeroeightsix.kami.module.modules.movement;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.EntityUtil;

/**
 * Created by Darki on 1/01/2019. (Complete Recode and added mode)
 */
@Module.Info(name = "Sprint", description = "Automatically makes the player sprint", category = Module.Category.MOVEMENT)
public class Sprint extends Module {

    private static Sprint instance = new Sprint();

    private Setting<Mode> mode = Settings.e("Mode", Mode.NORMAL);

    public Sprint() {
        instance = this;
        register(mode);
    }

    public enum Mode {
        NORMAL, LEGIT
    }

    @Override
    public void onUpdate() {
        switch (mode.getValue()) {
            case LEGIT:
                if (!EntityUtil.isPlayerMovingLegit()) {
                    mc.player.setSprinting(false);
                    return;
                }

                if (mc.player.collidedHorizontally) {
                    mc.player.setSprinting(false);
                    return;
                }

                mc.player.setSprinting(true);

            case NORMAL:
                if (!EntityUtil.isPlayerMovingKeybind()) {
                    mc.player.setSprinting(false);
                    return;
                }

                if (mc.player.collidedHorizontally) {
                    mc.player.setSprinting(false);
                    return;
                }

                mc.player.setSprinting(true);
        }

    }

}
