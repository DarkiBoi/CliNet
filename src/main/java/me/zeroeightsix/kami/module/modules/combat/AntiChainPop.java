package me.zeroeightsix.kami.module.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.TotemPopEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

/*
 * Hamburger made 21/02/2020
 */

@Module.Info(name = "AntiChainPop", description = "Stops Chain pops of Totems", category = Module.Category.COMBAT)
public class AntiChainPop extends Module {


    Module m;


    @EventHandler
    public Listener<TotemPopEvent> totemPopEvent = new Listener<>(event -> {
        EntityPlayer entity = (EntityPlayer) event.getEntity();
        if (mc.player == entity) {
            Command.sendChatMessage("test");
            m = ModuleManager.getModuleByName("Surround");
            if (!ModuleManager.isModuleEnabled("Surround")) m.toggle();
        }
    });

    @EventHandler
    public Listener<PacketEvent.Receive> totemPopListener = new Listener<>(event -> {

        if (mc.world == null || mc.player == null) {
            return;
        }

        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35) {
                Entity entity = packet.getEntity(mc.world);
                KamiMod.EVENT_BUS.post(new TotemPopEvent(entity));
            }
        }

    });
}
