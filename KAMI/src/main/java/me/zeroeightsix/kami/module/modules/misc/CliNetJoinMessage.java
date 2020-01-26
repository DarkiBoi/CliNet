package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import javax.swing.text.html.parser.Entity;

@Module.Info(name="CliNetJoinMessage", description = "Says \"CliNet On Top!\" when you join a server", category = Module.Category.MISC)
public class CliNetJoinMessage extends Module {

    @EventHandler
    public Listener<PacketEvent.Send> connectedToServerEvent = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketJoinGame) {
            mc.player.sendChatMessage("CliNet On Top!");
        }

    });



}
