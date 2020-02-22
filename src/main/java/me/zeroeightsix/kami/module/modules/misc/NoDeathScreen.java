package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;

@Module.Info(name = "NoDeathscreen", category = Module.Category.MISC)
public class NoDeathScreen extends Module {

    private GuiIngame guiIngame;

    @Override
    public void onUpdate() {
        if(mc.currentScreen instanceof GuiGameOver) {
            mc.player.respawnPlayer();
            mc.displayGuiScreen(null);
        }
    }


}
