package me.zeroeightsix.kami.mixin.client;

import me.zeroeightsix.kami.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by Darki on 17/12/2019.
 */
@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions extends MovementInput {

    private final GameSettings gameSettings = new GameSettings();
    private Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = "updatePlayerMoveState", at = @At("HEAD"), cancellable = true)
    public void updatePlayerMoveState(CallbackInfo info) {
        if (ModuleManager.getModuleByName("InventoryMove").isEnabled() && !(mc.currentScreen instanceof GuiChat)) {
            info.cancel();

            moveStrafe = 0.0F;
            moveForward = 0.0F;

            if(Keyboard.isKeyDown(gameSettings.keyBindForward.getKeyCode())) {
                ++moveForward;
            }

            if(Keyboard.isKeyDown(gameSettings.keyBindBack.getKeyCode())) {
                --moveForward;
            }

            if(Keyboard.isKeyDown(gameSettings.keyBindLeft.getKeyCode())) {
                ++moveStrafe;
            }

            if(Keyboard.isKeyDown(gameSettings.keyBindRight.getKeyCode())) {
                --moveStrafe;
            }

            jump = Keyboard.isKeyDown(gameSettings.keyBindJump.getKeyCode());
            sneak = Keyboard.isKeyDown(gameSettings.keyBindSneak.getKeyCode());

            if(Keyboard.isKeyDown(gameSettings.keyBindSprint.getKeyCode())) {
                mc.player.setSprinting(true);
            }

            if(sneak) {
                moveStrafe *= 0.3;
                moveForward *= 0.3;
            }
        } else {
            moveStrafe = 0.0F;
            moveForward = 0.0F;
        }

    }

}
