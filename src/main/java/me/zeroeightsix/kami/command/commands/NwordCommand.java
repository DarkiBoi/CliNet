package me.zeroeightsix.kami.command.commands;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.command.syntax.ChunkBuilder;
import me.zeroeightsix.kami.command.syntax.parsers.ModuleParser;
import net.minecraft.client.Minecraft;

/**
 * Created by Asikesa on 1/10/2020.
 */

public class NwordCommand extends Command {
    private final String blank = " \u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592\u2592";
    private final String line1 = " \u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588";
    private final String line2 = " \u2588\u2592\u2588\u2592\u2592\u2588\u2592\u2592\u2588\u2592\u2592\u2592\u2588\u2592\u2592\u2592\u2588\u2592\u2592\u2592\u2588\u2592\u2588";
    private final String line3 = " \u2588\u2592\u2588\u2592\u2592\u2588\u2592\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2592";
    private final String line4 = " \u2588\u2592\u2588\u2592\u2592\u2588\u2592\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2588\u2592\u2592\u2592\u2588\u2592\u2588";
    private final String line5 = " \u2588\u2592\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2588\u2588\u2592\u2588\u2592\u2588";
    private final String sender = " \uff34\uff48\uff49\uff53\u3000\uff4d\uff45\uff53\uff53\uff41\uff47\uff45\u3000\uff57\uff41\uff53\u3000\uff42\uff52\uff4f\uff55\uff47\uff48\uff54\u3000\uff54\uff4f\u3000\uff59\uff4f\uff55\u3000\uff42\uff59\u3000\uff23\uff4c\uff49\uff2e\uff45\uff54";
    public NwordCommand() {
        super("nword", new ChunkBuilder()
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

        if (Minecraft.getMinecraft().world.playerEntities.toString() == null) {
            Command.sendChatMessage("No players!");
            return;
        }

        if (playername.equals(Minecraft.getMinecraft().player.getName())) {
            Command.sendChatMessage("You cant send this to yourself retard");
            return;
        }

        sendNigger(playername);



    }

    public void sendNigger(String playername) {
        /*Minecraft.getMinecraft().player.sendChatMessage("/msg " + playername + line1);
        Minecraft.getMinecraft().player.sendChatMessage("/msg " + playername + line2);
        Minecraft.getMinecraft().player.sendChatMessage("/msg " + playername + line3);
        Minecraft.getMinecraft().player.sendChatMessage("/msg " + playername + line4);
        Minecraft.getMinecraft().player.sendChatMessage("/msg " + playername + line5);
        Minecraft.getMinecraft().player.sendChatMessage("/msg " + playername + " This message was brought to you by CliNet");*/

        Minecraft.getMinecraft().player.sendChatMessage("/msg " + playername  + " " + blank + "\n" + line1 + "\n" + line2 + "\n" + line3 + "\n" + line4 + "\n" + line5 + "\n" + blank);
        Minecraft.getMinecraft().player.sendChatMessage("/msg " + playername + " " + sender);
    }

}
