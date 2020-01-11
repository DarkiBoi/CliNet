package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;

/**
 * Created by 086 on 8/04/2018.
 * Updated by Darki on 12/12/19
 * Updated by TBM on 15/12/19
 * Updated by Asikesa on 1/9/2020
 * Updated by Hamburger on 8/01/2020
 */
@Module.Info(name = "ChatAppend", category = Module.Category.MISC, description = "Modifies your chat messages")
public class ChatAppend extends Module {

    private Setting<Boolean> commands = register(Settings.b("Commands", false));
    public static Setting<ChatAppend.Mode> mode = Settings.e("Mode", Mode.CLINET);

    //private final String KAMI_SUFFIX = " \u23D0 \u1D0B\u1D00\u1D0D\u026A"; //Uncomment this after beta
	private final String CLINET_SUFFIX = " \u23D0\u531a\uff9a\ua024\u20a6\u03b5\u0442 \u10ea\u04bd\u019a\u15e9";
    private final String CN_SUFFIX = " \u23D0 \uff23\uff4c\uff49\uff2e\uff45\uff54 \uff22\uff45\uff54\uff41";
    private final String CN_SUFFIX2 = " \u23D0 \u1455\u3125\ua024\ua2ca\u4e47\u271e";
    private final String CN_SUFFIX3 = " \u23D0 \ua253\ua492\ua024\ua2ca\u018e\u0287";
    private final String CN_SUFFIX4 = " \u23D0 \u058E\u13DF\u3058\uAB75\uAB3B\u2130\u2020\u14E4";
    //private final String PLIVID_SUFFIX = " \u23D0 \uff30\uff4c\uff49\uff56\uff49\uff44\uff0e\uff43\uff43"; //Uncomment this after beta
    private static ChatAppend INSTANCE = new ChatAppend();

    public ChatAppend() {
        INSTANCE = this;
        register(mode);
    }

    public enum Mode {
        CLINET, CLINET1, CLINET2, CLINET3, CLINET4
    }


    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") && !commands.getValue()) return;
            switch (mode.getValue()) {
                case CLINET:
				s += CLINET_SUFFIX;
				break;
				
				case CLINET2:
                    s += CN_SUFFIX2;
                    break;
                case CLINET1:
                    s += CN_SUFFIX;
                    break;
                case CLINET3:
                    s += CN_SUFFIX3;
                    break;
                case CLINET4:
                    s += CN_SUFFIX4;
                    break;
                /*case PLIVIDCC:
                    s += PLIVID_SUFFIX;
                    break;
                case KAMI:
                    s += KAMI_SUFFIX;
                    break;*/
            }
            if (ModuleManager.isModuleEnabled("ColourChat")){
                return;
            }
            if (s.length() >= 256) s = s.substring(0,256);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    });

}
