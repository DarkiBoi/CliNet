package me.zeroeightsix.kami.module.modules.render;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import io.netty.util.internal.MathUtil;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.ColourHolder;
import me.zeroeightsix.kami.util.Enemies;
import me.zeroeightsix.kami.util.EntityUtil;
import me.zeroeightsix.kami.util.Friends;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 19/12/2017.
 */
@Module.Info(name = "Nametags", description = "Draws descriptive nametags above entities", category = Module.Category.RENDER)
public class Nametags extends Module {

    private Setting<Boolean> players = register(Settings.b("Players", true));
    private Setting<Boolean> animals = register(Settings.b("Animals", false));
    private Setting<Boolean> mobs = register(Settings.b("Mobs", false));
    private Setting<Double> range = register(Settings.d("Range", 200));
    private Setting<Float> scale = register(Settings.floatBuilder("Scale").withMinimum(.5f).withMaximum(10f).withValue(1f).build());
    private Setting<Boolean> health = register(Settings.b("Health", true));
    private Setting<Boolean> pingSetting = register(Settings.b("Ping", true));

    RenderItem itemRenderer = mc.getRenderItem();

    private String str;

    public static Nametags instance = new Nametags();

    @Override
    public void onWorldRender(RenderEvent event) {
        if (mc.getRenderManager().options == null) return;

        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        Minecraft.getMinecraft().world.loadedEntityList.stream()
                .filter(EntityUtil::isLiving)
                .filter(entity -> !EntityUtil.isFakeLocalPlayer(entity))
                .filter(entity -> (entity instanceof EntityPlayer ? players.getValue() && mc.player != entity : (EntityUtil.isPassive(entity) ? animals.getValue() : mobs.getValue())))
                .filter(entity -> mc.player.getDistance(entity) < range.getValue())
                .sorted(Comparator.comparing(entity -> -mc.player.getDistance(entity)))
                .forEach(this::drawNametag);
        GlStateManager.disableTexture2D();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    public void drawNametag(Entity entityIn) {
        GlStateManager.pushMatrix();

        Vec3d interp = EntityUtil.getInterpolatedRenderPos(entityIn, mc.getRenderPartialTicks());
        float yAdd = entityIn.height + 0.5F - (entityIn.isSneaking() ? 0.25F : 0.0F);
        double x = interp.x;
        double y = interp.y + yAdd;
        double z = interp.z;

        float viewerYaw = mc.getRenderManager().playerViewY;
        float viewerPitch = mc.getRenderManager().playerViewX;
        boolean isThirdPersonFrontal = mc.getRenderManager().options.thirdPersonView == 2;
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);

        float f = mc.player.getDistance(entityIn);
        float m = (f / 8f) * (float) (Math.pow(1.2589254f, this.scale.getValue()));
        GlStateManager.scale(m, m, m);

        FontRenderer fontRendererIn = mc.fontRenderer;
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);

        DecimalFormat df = new DecimalFormat("##");

        String ping = null;
        try {
            ping = df.format(clamp(mc.getConnection().getPlayerInfo(entityIn.getUniqueID()).getResponseTime(), 0, 1000));
        } catch (NullPointerException e) {

        }

        float healthValue = Math.round(((EntityLivingBase) entityIn).getHealth());

        float goldHearts = ((EntityPlayer) entityIn).getAbsorptionAmount();

        float totalHealth = healthValue + goldHearts;

        if (totalHealth <= 36 && totalHealth >= 21) {
            str = (pingSetting.getValue() ? ping + "ms " : "") + entityIn.getName() + (health.getValue() ? " " + Command.SECTIONSIGN() + "6" + Math.round(((EntityLivingBase) entityIn).getHealth() + (entityIn instanceof EntityPlayer ? ((EntityPlayer) entityIn).getAbsorptionAmount() : 0)) : "");
        } else if (totalHealth <= 20 && totalHealth >= 15) {
            str = (pingSetting.getValue() ? ping + "ms " : "") + entityIn.getName() + (health.getValue() ? " " + Command.SECTIONSIGN() + "a" + Math.round(((EntityLivingBase) entityIn).getHealth() + (entityIn instanceof EntityPlayer ? ((EntityPlayer) entityIn).getAbsorptionAmount() : 0)) : "");
        } else if (totalHealth <= 14 && totalHealth >= 8) {
            str = (pingSetting.getValue() ? ping + "ms " : "") + entityIn.getName() + (health.getValue() ? " " + Command.SECTIONSIGN() + "e" + Math.round(((EntityLivingBase) entityIn).getHealth() + (entityIn instanceof EntityPlayer ? ((EntityPlayer) entityIn).getAbsorptionAmount() : 0)) : "");
        } else if (totalHealth <= 7 && totalHealth >= 4) {
            str = (pingSetting.getValue() ? ping + "ms " : "") + entityIn.getName() + (health.getValue() ? " " + Command.SECTIONSIGN() + "c" + Math.round(((EntityLivingBase) entityIn).getHealth() + (entityIn instanceof EntityPlayer ? ((EntityPlayer) entityIn).getAbsorptionAmount() : 0)) : "");
        } else if (totalHealth <= 3 && totalHealth >= 0) {
            str = (pingSetting.getValue() ? ping + "ms " : "") + entityIn.getName() + (health.getValue() ? " " + Command.SECTIONSIGN() + "4" + Math.round(((EntityLivingBase) entityIn).getHealth() + (entityIn instanceof EntityPlayer ? ((EntityPlayer) entityIn).getAbsorptionAmount() : 0)) : "");
        }


        int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder bufferbuilder = tessellator.getBuffer();

        glTranslatef(0, -20, 0);
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(-i - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        bufferbuilder.pos(-i - 1, 19, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        bufferbuilder.pos(i + 1, 19, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        bufferbuilder.pos(i + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        tessellator.draw();

        bufferbuilder.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(-i - 1, 8, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
        bufferbuilder.pos(-i - 1, 19, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
        bufferbuilder.pos(i + 1, 19, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
        bufferbuilder.pos(i + 1, 8, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();

        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        if (entityIn instanceof EntityPlayer) {
            if (Friends.isFriend(entityIn.getName())) {
                fontRendererIn.drawString(str, -i, 10, 0x11ee11);
            } else if (Enemies.isEnemy(entityIn.getName())) {
                fontRendererIn.drawString(str, -i, 10, 0xEE1111);
            } else {
                fontRendererIn.drawString(str, -i, 10, 0xffffff);
            }
        }

        GlStateManager.glNormal3f(0.0F, 0.0F, 0.0F);
        glTranslatef(0, 20, 0);

        GlStateManager.scale(-40, -40, 40);

        ArrayList<ItemStack> equipment = new ArrayList<>();
        entityIn.getHeldEquipment().forEach(itemStack -> {
            if (itemStack != null) equipment.add(itemStack);
        });
        ArrayList<ItemStack> armour = new ArrayList<>();
        entityIn.getArmorInventoryList().forEach(itemStack -> {
            if (itemStack != null) armour.add(itemStack);
        });
        Collections.reverse(armour);
        equipment.addAll(armour);
        if (equipment.size() == 0) {
            GlStateManager.popMatrix();
            return;
        }

        Collection<ItemStack> a = equipment.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        GlStateManager.translate(((a.size() - 1) / 2f) * .5f, .6, 0);

        a.forEach(itemStack -> {
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.scale(.5, .5, 0);
            GlStateManager.disableLighting();
            this.itemRenderer.zLevel = -5;
            this.itemRenderer.renderItem(itemStack, itemStack.getItem() == Items.SHIELD ? ItemCameraTransforms.TransformType.FIXED : ItemCameraTransforms.TransformType.NONE);
            this.itemRenderer.zLevel = 0;
            GlStateManager.scale(2, 2, 0);
            GlStateManager.popAttrib();
            GlStateManager.translate(-.5f, 0, 0);
        });

        GlStateManager.popMatrix();

    }

    public static float clamp(float val, float min, float max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }


}
