package me.zeroeightsix.kami.command.commands;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.command.syntax.ChunkBuilder;

/**
 * Created by Hamburger on 15/01/2020.
 */



public class AutoReplyCommand extends Command {

    public static String areply = "";

    public AutoReplyCommand() {
        super("autoreply", new ChunkBuilder().append("string").build());
    }

    @Override
    public void call(String[] args) {
        if (args.length == 0) {
            Command.sendChatMessage("Please enter a valid response");
            return;
        }
        int i = 0;
        areply = "";
        while (args.length - 1 > i){
            areply += " " + args[i];
            i++;
        }
        Command.sendChatMessage("Response set to &b" + areply);
    }

}