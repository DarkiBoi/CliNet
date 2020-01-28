package me.zeroeightsix.kami.mixin.client;

import me.zeroeightsix.kami.gui.mc.login.GuiAltLogin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.List;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {

    @Shadow
    List<GuiButton> buttonList;

    @Shadow int width;
    @Shadow int height;
    @Shadow GuiButton button;

    @Shadow GuiMainMenu guiMainMenu;

    @Shadow
    Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = "drawScreen", at = @At("TAIL"), cancellable = true)
    public void drawScreen() {
        buttonList.add(new GuiButton(500, this.width / 2 - 100,  this.height / 4 + 48 + 24 * 2 * 48, "AltManager"));
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"), cancellable = true)
    public void actionPerformed() {
        if(button.id == 500) {
            this.mc.displayGuiScreen(new GuiAltLogin(guiMainMenu));
        }
    }


}
