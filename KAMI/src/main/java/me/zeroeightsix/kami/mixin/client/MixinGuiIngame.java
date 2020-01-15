package me.zeroeightsix.kami.mixin.client;

import jdk.nashorn.internal.codegen.CompilerConstants;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.chat.IChatListener;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {

    @Shadow @Final
    protected Map<ChatType, List<IChatListener>> chatListeners;

    @Inject(method="addChatMessage", at = @At("HEAD"), cancellable = true)
    public void addChatMessage(ChatType chatTypeIn, ITextComponent message, CallbackInfo ci) {
        ci.cancel();
        this.chatListeners.get(chatTypeIn).forEach(iChatListener -> {
            iChatListener.say(chatTypeIn,  message); // EDIT THE MESSAGE TO BE A NEW ITEXTCOMPONENT
        });
    }

}
