package me.zeroeightsix.kami.module.modules.movement;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.MoverType;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Created by Darki on 17/12/2019.
 * @see me.zeroeightsix.kami.mixin.client.MixinMovementInputFromOptions
 */
@Module.Info(name = "InventoryMove", description = "Allows you to move while any GUI container is open", category = Module.Category.MOVEMENT)
public class InventoryMove extends Module {

    @Override
    public void onUpdate() {

        if(mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            if(Keyboard.isKeyDown(200)) {
                mc.player.rotationPitch -= 5.0F;
            }
            if(Keyboard.isKeyDown(208)) {
                mc.player.rotationPitch += 5.0F;
            }
            if(Keyboard.isKeyDown(203)) {
                mc.player.rotationYaw -= 7.0F;
            }
            if(Keyboard.isKeyDown(205)) {
                mc.player.rotationYaw += 7.0F;
            }
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                if (mc.player.onGround) {
                    mc.player.jump();
                }
            }
        }

    }

}
