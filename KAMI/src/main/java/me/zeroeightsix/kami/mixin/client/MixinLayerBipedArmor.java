package me.zeroeightsix.kami.mixin.client;


import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerBipedArmor.class)
public class MixinLayerBipedArmor {

    @Inject(method = "setModelSlotVisible", at = @At(value = "HEAD"), cancellable = true)
    protected void setModelSlotVisible(ModelBiped model, EntityEquipmentSlot slotIn, CallbackInfo info) {
        if (ModuleManager.isModuleEnabled("AntiArmourRender")) {
            info.cancel();
            switch (slotIn) {
                case HEAD:
                    model.bipedHead.showModel = false;
                    model.bipedHeadwear.showModel = false;
                case CHEST:
                    model.bipedBody.showModel = false;
                    model.bipedRightArm.showModel = false;
                    model.bipedLeftArm.showModel = false;
                case LEGS:
                    model.bipedBody.showModel = false;
                    model.bipedRightLeg.showModel = false;
                    model.bipedLeftLeg.showModel = false;
                case FEET:
                    model.bipedRightLeg.showModel = false;
                    model.bipedLeftLeg.showModel = false;            }





        }
    }

}
/*
switch (slotIn)
        {
        case HEAD:
        p_188359_1_.bipedHead.showModel = true;
        p_188359_1_.bipedHeadwear.showModel = true;
        break;
        case CHEST:
        p_188359_1_.bipedBody.showModel = true;
        p_188359_1_.bipedRightArm.showModel = true;
        p_188359_1_.bipedLeftArm.showModel = true;
        break;
        case LEGS:
        p_188359_1_.bipedBody.showModel = true;
        p_188359_1_.bipedRightLeg.showModel = true;
        p_188359_1_.bipedLeftLeg.showModel = true;
        break;
        case FEET:
        p_188359_1_.bipedRightLeg.showModel = true;
        p_188359_1_.bipedLeftLeg.showModel = true;
        }

 */