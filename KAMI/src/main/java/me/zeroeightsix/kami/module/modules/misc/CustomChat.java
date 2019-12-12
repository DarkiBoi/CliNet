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
 */
@Module.Info(name = "CustomChat", category = Module.Category.MISC, description = "Modifies your chat messages")
public class CustomChat extends Module {

    private Setting<Boolean> commands = register(Settings.b("Commands", false));
    public static Setting<CustomChat.Mode> mode = Settings.e("Mode", Mode.CliNet);

    private final String KAMI_SUFFIX = " \u23D0 \u1D0B\u1D00\u1D0D\u026A";
    private final String CN_SUFFIX = " | CliNet Beta";

    private static CustomChat INSTANCE = new CustomChat();

    public CustomChat() {
        INSTANCE = this;
        register(mode);
    }

    public enum Mode {
        KAMI, CliNet
    }

    @Override
    public void onDisable() {
        Module module = ModuleManager.getModuleByName("CustomChat");
        module.enable();
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
                case CliNet:
                    s += CN_SUFFIX;
                    break;
            }
            if (s.length() >= 256) s = s.substring(0,256);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    });

}
