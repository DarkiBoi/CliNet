package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.gui.kami.theme.kami.KamiActiveModulesUI;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import me.zeroeightsix.kami.module.modules.misc.CustomChat;
import me.zeroeightsix.kami.module.ModuleManager;
/**
 * Created by Hamburger on 4/1/2020.
 * Credits to 086 for packet base
 */
@Module.Info(name = "AutoChatCo", category = Module.Category.MISC, description = "Auto options for the ChatCo plugin")
public class AutoChatCo extends Module {


    private Setting<Boolean> autotext = register(Settings.b("AutoText", true));
    private Setting<Boolean> autoshowspoiler = register(Settings.b("AutoSpoilerShown", false));
    private Setting<ChatCoMode> mode = register(Settings.e("Mode", ChatCoMode.GREENTEXT));

    public enum ChatCoMode{
        GREENTEXT, BLUETEXT, SPOILER
    }



    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") | s.startsWith(".")) {
                return;
            }
            if(autotext.getValue()) {


                switch (mode.getValue()) {
                    case GREENTEXT:
                        s = ">" + s;
                        break;
                    case BLUETEXT:
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
        }) ;
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
