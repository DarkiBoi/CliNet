package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.server.SPacketChat;

import java.util.List;

@Module.Info(name = "AntiSpam", description = "Detects spam and blocks it", category = Module.Category.MISC)
public class AntiSpam extends Module {

    private Setting<Boolean> discordLinks = register(Settings.b("Discord Invites", true));
    private Setting<Boolean> announcer = register(Settings.b("Announcer", true));



    @EventHandler
    public Listener<PacketEvent.Receive> listener = new Listener<>(event -> {

        if(mc.player == null || this.isDisabled()) return;

        if(!(event.getPacket() instanceof SPacketChat)) return;

        SPacketChat chatMessage = (SPacketChat) event.getPacket();


        if(detectSpam(chatMessage.chatComponent.getUnformattedText())) {
            event.cancel();
        }
    });




    private boolean detectSpam(String message) {
        if(discordLinks.getValue()) {
            for (String discordSpam : discordStringArray) {
                if(message.contains(discordSpam)) {
                    return true;
                }
            }
        }

        if(announcer.getValue()) {
            for (String announcerSpam : announcerStringArray) {
                if(message.contains(announcerSpam)) {
                    return true;
                }
            }
        }

        return false;
    }



    private static String[] discordStringArray =
            {
                    "discord.gg",
            };

    private static String[] announcerStringArray =
            {
                    "Looking for new anarchy servers?",
                    "I just walked",
                    "I just flew",
                    "I just placed",
                    "I just ate",
                    "I just healed",
                    "I just took"
            };

}
