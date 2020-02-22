package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

/**
 * Created by Darki on 16/12/2019.
 */
@Module.Info(name = "Timer", description = "Makes your game faster", category = Module.Category.MISC)
public class Timer extends Module {

    private Setting<Float> speed = register(Settings.floatBuilder("Speed").withMinimum(0f).withMaximum(10f).withValue(4.2f));

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50;
    }

    @Override
    public void onUpdate() {
        mc.timer.tickLength = 50.0f / speed.getValue();
        this.setHudInfo(speed.getValue().toString());
    }

}
