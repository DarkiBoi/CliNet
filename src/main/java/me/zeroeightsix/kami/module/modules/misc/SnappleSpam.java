package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.SnappleFacts;

@Module.Info(name = "SnappleSpam", description = "Spams snapple facts", category = Module.Category.MISC)
public class SnappleSpam extends Module {

    private long starttime = 0;
    private Setting<Float> delay = register(Settings.floatBuilder("Delay").withMinimum(7f).withMaximum(60f).withValue(10f));


    @Override
    public void onUpdate()
    {
        if(System.currentTimeMillis() - starttime >=  delay.getValue().longValue() * 1000){
            mc.player.sendChatMessage(SnappleFacts.randomfact());
            starttime = System.currentTimeMillis();
        }
    }

    @Override
    public void onEnable(){
    starttime = System.currentTimeMillis();

    }
}
