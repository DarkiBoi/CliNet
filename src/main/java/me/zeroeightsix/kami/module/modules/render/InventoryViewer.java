package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.gui.kami.KamiGUI;
import me.zeroeightsix.kami.gui.rgui.component.container.use.Frame;
import me.zeroeightsix.kami.gui.rgui.util.ContainerHelper;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.ColourUtils;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

/**
 * Thanks to Bella uwu nah fuck you created by me XDDD
 */

@Module.Info(name = "InventoryViewer", category = Module.Category.HIDDEN)
public class InventoryViewer extends Module {

    public boolean visible = false;

    KamiGUI kamiGUI = KamiMod.getInstance().getGuiManager();
    private int invPos(int i) {
        kamiGUI = KamiMod.getInstance().getGuiManager();
        if (kamiGUI != null) {
            List<Frame> frames = ContainerHelper.getAllChildren(Frame.class, kamiGUI);
            for (Frame frame : frames) {
                if (!frame.getTitle().equalsIgnoreCase("inventory viewer")) continue;
                switch (i) {
                    case 0:
                        return frame.getX();
                    case 1:
                        return frame.getY();
                    default:
                        return 0;
                }
            }
        }
        return 0;
    }

    private boolean isPinned() {
        kamiGUI = KamiMod.getInstance().getGuiManager();
        if(kamiGUI != null) {
            List<Frame> frames = ContainerHelper.getAllChildren(Frame.class, kamiGUI);
            for(Frame frame : frames) {
                if (!frame.getTitle().equalsIgnoreCase("inventory viewer")) continue;
                return frame.isPinned();
             }
        }

        return false;

    }

    @Override
    public void onDisable() {
        this.enable();
    }

    private static void preItemRender() {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
    }

    private static void postItemRender() {
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    @Override
    public void onRender() {
        final NonNullList<ItemStack> items = InventoryViewer.mc.player.inventory.mainInventory;
        if(isPinned()) {
            boxRender(invPos(0), invPos(1));
            itemRender(items, invPos(0), invPos(1));
        }
    }

    private void boxRender(final int x, final int y) {
        KamiTessellator.drawRect(x + 165, y + 56, x,  y, 0x75101010);
    }

    private void itemRender(final NonNullList<ItemStack> items, final int x, final int y) {
        for (int size = items.size(), item = 9; item < size; ++item) {
            final int slotX = x + 1 + item % 9 * 18;
            final int slotY = y + 1 + (item / 9 - 1) * 18;
            preItemRender();
            mc.getRenderItem().renderItemAndEffectIntoGUI(items.get(item), slotX, slotY);
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, items.get(item), slotX, slotY);
            postItemRender();
        }
    }
}
