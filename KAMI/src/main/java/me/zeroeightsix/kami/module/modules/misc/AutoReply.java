package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.SnappleFacts;
import net.minecraft.network.play.server.SPacketChat;
import me.zeroeightsix.kami.command.commands.AutoReplyCommand;

/*
* Created by Hamburger on 15/01/2020
 */


@Module.Info(name = "AutoReply", category = Module.Category.MISC, description = "automatically replies to messages with a custom message")
public class AutoReply extends Module {

    private Setting<Mode> mode = register(Settings.e("Mode", Mode.SNAPPLE));

    public enum Mode {
        CUSTOM, SNAPPLE
    }

    @EventHandler
    private Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketChat) {
            String r = ((SPacketChat) event.getPacket()).getChatComponent().getUnformattedText();
            if (r.contains("whispers: ")) {
                String msg = "";

                switch (mode.getValue()){
                    case CUSTOM:
                        msg = AutoReplyCommand.areply;
                        break;
                    case SNAPPLE:
                        msg = SnappleFacts.randomfact();
                }
                mc.player.sendChatMessage("/r " + msg);
            }

        }

    });

}