package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.Random;

/**
 * Created by 086 on 8/04/2018.
 * Updated by Darki on 12/12/19
 * Updated by TBM on 15/12/19
 * Updated by Asikesa on 1/9/2020
 */
@Module.Info(name = "ChatAppend", category = Module.Category.MISC, description = "Modifies your chat messages")
public class ChatAppend extends Module {

    private Setting<Boolean> commands = register(Settings.b("Commands", false));
    private Setting<Boolean> blue = register(Settings.b("Blue", false));
    private Setting<Boolean> version = register(Settings.b("Version", true));
    private Setting<ChatAppend.Mode> mode = register(Settings.e("Mode", Mode.CLINET));

	private final String CN_SUFFIX = " \u23D0 \uff23\uff4c\uff49\uff2e\uff45\uff54";
    private final String CN_SUFFIX2 = " \u23D0 \u1d04\u029f\u026a\u0274\u1d07\u1d1b";
    private final String CN_SUFFIX3 = " \u23D0 CliNet";

    public enum Mode {
        CLINET, CLINET2, PLAINTEXT, RANDOM
    }


    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") && !commands.getValue()) return;
            switch (mode.getValue()) {
                case CLINET:
                    s+=doChatAppend(CN_SUFFIX);
                    if (version.getValue()) s+= (" \u23D0 " + KamiMod.MODVER);
                    break;
                case CLINET2:
                    s+= doChatAppend(CN_SUFFIX2);
                    if (version.getValue()) s+= (" \u23D0 " + KamiMod.MODVER);
                    break;
                case PLAINTEXT:
                    s+= doChatAppend(CN_SUFFIX3);
                    if (version.getValue()) s+= (" \u23D0 " + KamiMod.MODVER);
                    break;
                case RANDOM:
                    String[] stringlist = {CN_SUFFIX, CN_SUFFIX2, CN_SUFFIX3};
                    Random r = new Random();
                    String randomsuffix = stringlist[r.nextInt(stringlist.length)];
                    s += doChatAppend(randomsuffix);
                    if (version.getValue()) s+= (" \u23D0 " + KamiMod.MODVER);
                    break;


            }
            if (ModuleManager.isModuleEnabled("ColourChat")) {
                return;
            }
            if (s.length() >= 256) s = s.substring(0,256);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    });

    public String doChatAppend(String chatAppend) {
        if (blue.getValue() == true) {
            return "`" + chatAppend;
        } else {
            return chatAppend;
        }
    }

}
