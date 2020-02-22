package me.zeroeightsix.kami.mixin.client;

import me.zeroeightsix.kami.module.ModuleManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {

    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    public void hasEffect(CallbackInfoReturnable<Boolean> cir) {
        if(ModuleManager.isModuleEnabled("Glimmer"))
        cir.setReturnValue(true);
    }

}
