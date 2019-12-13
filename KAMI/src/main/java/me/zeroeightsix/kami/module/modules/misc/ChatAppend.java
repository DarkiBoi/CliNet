package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.gui.Themes;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;

/**
 * Created by 086 on 8/04/2018.
 * Updated by Darki on 12/12/19
 */
@Module.Info(name = "ChatAppend", category = Module.Category.MISC, description = "Modifies your chat messages")
public class ChatAppend extends Module {

    private Setting<Boolean> commands = register(Settings.b("Commands", false));
    public static Setting<ChatAppend.Mode> mode = Settings.e("Mode", Mode.CLINET);

    private final String KAMI_SUFFIX = " \u23D0 \u1D0B\u1D00\u1D0D\u026A";
    private final String CN_SUFFIX = " | CliNet Beta";

    private static ChatAppend INSTANCE = new ChatAppend();

    public ChatAppend() {
        INSTANCE = this;
        register(mode);
    }

    public enum Mode {
        KAMI, CLINET
    }


    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") && !commands.getValue()) return;
            switch (mode.getValue()) {
                case KAMI:
                    s += KAMI_SUFFIX;
                    break;
                case CLINET:
                    s += CN_SUFFIX;
                    break;
            }
            if (s.length() >= 256) s = s.substring(0,256);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    });

}
