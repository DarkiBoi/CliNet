package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;

/**
 * Created by TBM on 12/12/19
 * Updated by Darki on 13/12/19
 * Updated by Hamburger on 04/01/20
 */
@Module.Info(name = "ColourChat", category = Module.Category.MISC, description = "Modifies your chat messages to be colourful. Designed for the ChatCo Plugin")
public class ColourChat extends Module {

    private Setting<Boolean> autotext = register(Settings.b("AutoText", true));
    private Setting<Boolean> autoshowspoiler = register(Settings.b("AutoSpoilerShown", false));
    private Setting<Colour> colour = register(Settings.e("Colour", Colour.GREEN));

    public enum Colour {
        GREEN, BLUE, SPOILER
    }


    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") | s.startsWith(".")) {
                return;
            }
            if (autotext.getValue()) {

                switch (colour.getValue()) {
                    case GREEN:
                        s = ">" + s;
                        break;
                    case BLUE:
                        s = "`" + s;
                        break;
                    case SPOILER:
                        //if(ModuleManager.isModuleEnabled("ChatAppend")) {
                        //CustomChat cc = new CustomChat();      Name was changed so commenting this out ftm
                        //String suffix = cc.KAMI_SUFFIX;
                        s = "[SPOILER]" + s + "[/SPOILER]";

                        break;
                }
            }

            if (s.length() >= 256) s = s.substring(0, 256);
            ((CPacketChatMessage) event.getPacket()).message = s;


        }
    });
    @EventHandler
    private Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketChat) {
            String r = ((SPacketChat) event.getPacket()).getChatComponent().getUnformattedText();
            if (autoshowspoiler.getValue()) {
                if (r == "SPOILER") {
                    mc.player.sendChatMessage("show spoiler");
                }
            }
        }
    });

}