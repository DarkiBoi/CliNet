package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

@Module.Info(name = "TimerSwitch", description = "A thing to use fast timer for a short time and go fast", category = Module.Category.MISC)
public class TimerSwitch extends Module {

    private int tickWait = 0;

    private Setting<Float> speedUsual = register(Settings.floatBuilder("Speed").withMinimum(1f).withMaximum(10f).withValue(4.2f));
    private Setting<Float> fastUsual = register(Settings.floatBuilder("Fast Speed").withMinimum(1f).withMaximum(200f).withValue(10f));
    private Setting<Float> tickToFast = register(Settings.floatBuilder("Tick To Fast").withMinimum(0f).withMaximum(20f).withValue(4f));
    private Setting<Float> tickToNoFast = register(Settings.floatBuilder("Tick To Disable Fast").withMinimum(0f).withMaximum(20f).withValue(7f));

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50;
    }

    @Override
    public void onUpdate() {
        if (tickWait == tickToFast.getValue()) {
            mc.timer.tickLength = 50.0f / fastUsual.getValue();
            this.setHudInfo(fastUsual.getValue().toString());
        }
        if (tickWait >= tickToNoFast.getValue()) {
            tickWait = 0;
            mc.timer.tickLength = 50.0f / speedUsual.getValue();
            this.setHudInfo(speedUsual.getValue().toString());
        }
        tickWait++;
    }


}
