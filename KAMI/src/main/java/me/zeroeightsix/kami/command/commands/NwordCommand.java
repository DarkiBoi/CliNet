package me.zeroeightsix.kami.command.commands;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.command.syntax.ChunkBuilder;
import me.zeroeightsix.kami.command.syntax.parsers.ModuleParser;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.network.play.client.CPacketChatMessage;
 import me.zeroeightsix.kami.event.events.RenderEvent;
 import me.zeroeightsix.kami.module.Module;
 import me.zeroeightsix.kami.module.Module.Info;
 import me.zeroeightsix.kami.util.EntityUtil;
 import me.zeroeightsix.kami.util.Wrapper;
 import net.minecraft.client.Minecraft;
 import net.minecraft.entity.Entity;
/**
 * Created by Asikesa on 1/10/2020.
 */

public class NwordCommand extends Command {
 private final String line1= " \u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588";
  private final String line2 = " \u2588\u2592\u2588\u2592\u2592\u2588\u2592\u2592\u2588\u2592\u2592\u2592\u2588\u2592\u2592\u2592\u2588\u2592\u2592\u2592\u2588\u2592\u2588";
   private final String line3 = " \u2588\u2592\u2588\u2592\u2592\u2588\u2592\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2592";
    private final String line4 = " \u2588\u2592\u2588\u2592\u2592\u2588\u2592\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2592\u2592\u2588\u2592\u2588";
	 private final String line5 = " \u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2592\u2588";
    public NwordCommand() {
        super("Nword", new ChunkBuilder()
                .append("Player", true, new ModuleParser())
                .build());
    }

    @Override
    public void call(String[] args) {
        if (args.length == 0) {
            Command.sendChatMessage("Please specify a player!");
            return;
        }

		String playername = (args[0]);

        Wrapper.getPlayer().sendChatMessage("/msg " + playername + line1);
        Wrapper.getPlayer().sendChatMessage("/msg " + playername + line2);
        Wrapper.getPlayer().sendChatMessage("/msg " + playername + line3);
        Wrapper.getPlayer().sendChatMessage("/msg " + playername + line4);
        Wrapper.getPlayer().sendChatMessage("/msg " + playername + line5);
		Wrapper.getPlayer().sendChatMessage("/msg " + playername + " This message was brought to you by CliNet");


		
    }
}
