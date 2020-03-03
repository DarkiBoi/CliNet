package me.zeroeightsix.kami.gui.kami.theme.kami;

import me.zeroeightsix.kami.gui.kami.RootFontRenderer;
import me.zeroeightsix.kami.gui.kami.RootSmallFontRenderer;
import me.zeroeightsix.kami.gui.kami.component.ColourPickerButton;
import me.zeroeightsix.kami.gui.kami.component.ColourPickerButtonRainbow;
import me.zeroeightsix.kami.gui.kami.component.EnumButton;
import me.zeroeightsix.kami.gui.rgui.component.container.Container;
import me.zeroeightsix.kami.gui.rgui.render.AbstractComponentUI;
import me.zeroeightsix.kami.gui.rgui.render.font.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 8/08/2017.
 */
public class KamiColourPickerButtonRainbowUI extends AbstractComponentUI<ColourPickerButtonRainbow> {

    RootSmallFontRenderer smallFontRenderer = new RootSmallFontRenderer();
    RootFontRenderer normalFontRenderer = new RootFontRenderer(1.0f);

    protected Color idleColour = new Color(163, 163, 163);
    protected Color downColour = new Color(255, 255, 255);

    EnumButton modeComponent;
    long lastMS = System.currentTimeMillis();

    @Override
    public void renderComponent(ColourPickerButtonRainbow component, FontRenderer fontRenderer) {
        if (System.currentTimeMillis() - lastMS > 3000 && modeComponent != null) {
            modeComponent = null;
        }

        int c = component.isPressed() ? 0xaaaaaa : 0xdddddd;
        if (component.isHovered())
            c = (c & 0x7f7f7f) << 1;

        //RenderHelper.drawRoundedRectangle(0,0,component.getWidth(), component.getHeight(), 3f);

        glColor3f(1,1,1);
        glEnable(GL_TEXTURE_2D);

        int parts = component.getModes().length;
        double step = component.getWidth() / (double) parts;
        double startX = step * component.getIndex();
        double endX = step * (component.getIndex()+1);

        int height = component.getHeight();
        float downscale = 0.9f;

        glDisable(GL_TEXTURE_2D);
        glColor3f(.59f,.05f,.11f);
        glBegin(GL_LINES);
        {
            glVertex2d(startX,height/downscale);
            glVertex2d(endX,height/downscale);
        }
        glEnd();

        if (modeComponent == null || !modeComponent.equals(component)) {
            smallFontRenderer.drawString(0,1,c,component.getName());
            if (component.getIndexMode() == "RB") normalFontRenderer.drawString(component.getWidth() - fontRenderer.getStringWidth(component.getIndexMode()),0,c, component.getIndexMode());
            else normalFontRenderer.drawString(component.getWidth() - fontRenderer.getStringWidth(component.getIndexMode()),0,c, "\u00A7" + component.getIndexMode() + component.getIndexMode());
        }else {
            if (component.getIndexMode() == "RB")normalFontRenderer.drawString(component.getWidth() / 2 - fontRenderer.getStringWidth(component.getIndexMode()) / 2, 0,c, component.getIndexMode());
            else normalFontRenderer.drawString(component.getWidth() / 2 - fontRenderer.getStringWidth(component.getIndexMode()) / 2, 0,c,  "\u00A7" + component.getIndexMode() + component.getIndexMode());
        }


        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void handleSizeComponent(ColourPickerButtonRainbow component) {
        int width = 0;
        for (String s : component.getModes()) {
            width = Math.max(width, smallFontRenderer.getStringWidth(s));
        }
        component.setWidth(smallFontRenderer.getStringWidth(component.getName()) + width + 1);
        component.setHeight(smallFontRenderer.getFontHeight()+2);
    }

    @Override
    public void handleAddComponent(ColourPickerButtonRainbow component, Container container) {
        component.addPoof(new EnumButton.EnumbuttonIndexPoof<EnumButton, EnumButton.EnumbuttonIndexPoof.EnumbuttonInfo>() {
            @Override
            public void execute(EnumButton component, EnumbuttonInfo info) {
                modeComponent = component;
                lastMS = System.currentTimeMillis();
            }
        });
    }


}
