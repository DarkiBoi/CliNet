package me.zeroeightsix.kami.mixin.client;

import me.zeroeightsix.kami.module.modules.render.AntiArmourRender;
import net.minecraft.client.model.ModelBiped;

import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by TBM on 30/12/2019.
 */
@Mixin(LayerArmorBase.class)
public abstract class MixinLayerArmorBase {


    @Inject(method = "renderArmorLayer", at=@At("HEAD"), cancellable = true)
    public void onRenderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo ci) {
        if (AntiArmourRender.INSTANCE.isEnabled()) {
            if (!(AntiArmourRender.INSTANCE.player.getValue()) && entityLivingBaseIn instanceof EntityPlayer) {
                if (!AntiArmourRender.shouldRenderPiece(slotIn)) ci.cancel();
            } else if (!(AntiArmourRender.INSTANCE.armourstand.getValue()) && entityLivingBaseIn instanceof EntityArmorStand) {
                if (!AntiArmourRender.shouldRenderPiece(slotIn)) ci.cancel();
            } else if (!(AntiArmourRender.INSTANCE.mobs.getValue()) && entityLivingBaseIn instanceof EntityMob) {
                if (!AntiArmourRender.shouldRenderPiece(slotIn)) ci.cancel();
            }
        }

    }




}