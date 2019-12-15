package me.zeroeightsix.kami.gui.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.gui.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;

/**
 * Created by TBM on 12/12/19
 * Updated by Darki on 13/12/19
 */
@Module.Info(name = "ColourChat", category = Module.Category.MISC, description = "Modifies your chat messages to add > for green or ` for blue in front")
public class ColourChat extends Module {

    public static Setting<Colour> colour = Settings.e("Colour", Colour.GREEN);

    private final String GREEN_PREFIX = ">";
    private final String BLUE_PREFIX = "`";

    private static ColourChat INSTANCE = new ColourChat();

    public ColourChat() {
        INSTANCE = this;
        register(colour);
    }

    public enum Colour {
        GREEN, BLUE
    }


    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/")) return;
            switch (colour.getValue()) {
                case GREEN:
                    s = GREEN_PREFIX + s;
                    break;
                case BLUE:
                    s = BLUE_PREFIX + s;
                    break;
            }
            if (s.length() >= 256) s = s.substring(0,256);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    });


}
