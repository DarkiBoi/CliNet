package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

@Module.Info(name="ChatNameHighlight", description = "Highlights player names in chat", category = Module.Category.MISC)
public class ChatNameHighlight extends Module {

}
