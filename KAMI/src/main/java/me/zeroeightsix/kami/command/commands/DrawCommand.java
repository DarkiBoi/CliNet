package me.zeroeightsix.kami.command.commands;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.command.syntax.ChunkBuilder;
import me.zeroeightsix.kami.command.syntax.parsers.ModuleParser;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;

public class DrawCommand extends Command {

    public DrawCommand() {
        super("draw", new ChunkBuilder()
                .append("[module]", true, new ModuleParser())
                .build()
        );
    }

    @Override
    public void call(String[] args) {
        if (args.length == 1) {
            Command.sendChatMessage("Please specify a module.");
            return;
        }

        String module = args[0];

        Module m = ModuleManager.getModuleByName(module);

        if(m.isShowOnArray() == false) {
            m.setShowOnArray(true);
            sendChatMessage("Undrawn " + m.getName());
            return;
        }

        if (m == null){
            sendChatMessage("Unknown module '" + module + "'!");
            return;
        }

        m.setShowOnArray(false);

        sendChatMessage("Drawn " + m.getName());

    }
}
