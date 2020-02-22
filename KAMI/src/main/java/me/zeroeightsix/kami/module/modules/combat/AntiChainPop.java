package me.zeroeightsix.kami.module.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.TotemPopEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.entity.player.EntityPlayer;

/*
 * Hamburger made 21/02/2020
 */


@Module.Info(name = "AntiChainPop", description = "Stops Chain pops of Totems", category = Module.Category.COMBAT)
public class AntiChainPop extends Module {

    private Setting<Mode> mode = register(Settings.e("Mode", Mode.SURROUND));
    Module m;


    @EventHandler
    public Listener<TotemPopEvent> totemPopEvent = new Listener<>(event -> {
        EntityPlayer entity = (EntityPlayer) event.getEntity();
        if (mc.player == entity)
            switch(mode.getValue()) {
                case SURROUND:
                    m = ModuleManager.getModuleByName("Surround");
                    if (!ModuleManager.isModuleEnabled("Surround")) m.toggle();
                break;
                case PACKET:
                    //Placebo lmao, will try something at a later time
            }
    });

    private enum Mode {
        SURROUND, PACKET
    }
}
