package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.games.Pong;
import me.zeroeightsix.kami.module.Module;
import org.lwjgl.opengl.Display;

@Module.Info(name = "Pong", description = "Pong", category = Module.Category.MISC)
public class PongModule extends Module {

    private final Pong pong = new Pong();


    @Override
    public void onEnable() {

        Pong.createDisplay();

        while(!Display.isCloseRequested()) {

            //game logic
            //render
            Pong.updateDisplay();

        }

        Pong.closeDisplay();

    }

    @Override
    public void onDisable() {

    }

}
