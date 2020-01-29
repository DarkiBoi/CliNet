package me.zeroeightsix.kami.module.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Module.Info(name = "ChatTimestamps", description = "Shows timestamps next to messages in chat", category = Module.Category.MISC)
public class ChatTimestamps extends Module {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("<HH:mm:ss> ");
    LocalDateTime now = LocalDateTime.now();

    @EventHandler
    public Listener<ClientChatReceivedEvent> listener = new Listener<>(event -> {
        if(this.isDisabled()) {
            return;
        }

        TextComponentString m = new TextComponentString(ChatFormatting.GRAY + dtf.format(now) + ChatFormatting.RESET);
        m.appendSibling(event.getMessage());

        event.setMessage(m);
    });




}
