package me.zeroeightsix.kami.gui.kami.theme.kami;

import me.zeroeightsix.kami.gui.kami.RenderHelper;
import me.zeroeightsix.kami.gui.kami.component.SettingsPanel;
import me.zeroeightsix.kami.gui.rgui.render.AbstractComponentUI;
import me.zeroeightsix.kami.gui.rgui.render.font.FontRenderer;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.gui.Themes;
import me.zeroeightsix.kami.setting.Setting;
import net.minecraft.client.Minecraft;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 16/12/2017.
 */
public class KamiSettingsPanelUI extends AbstractComponentUI<SettingsPanel> {

    @Override
    public void renderComponent(SettingsPanel component, FontRenderer fontRenderer) {
        super.renderComponent(component, fontRenderer);
//        glLineWidth(2);
//        glColor3f(.59f,.05f,.11f);
//        glBegin(GL_LINES);
//        {
//            glVertex2d(0,component.getHeight());
//            glVertex2d(component.getWidth(),component.getHeight());
//        }
//        glEnd();


        for (Setting setting : ModuleManager.getModuleByName("Themes").settingList) {
            if (Themes.mode.getValue() == Themes.Theme.MODERN) {
                glLineWidth(2f);
                if (component.getHeight() != 12) {
                    glColor3f(0.13f, 0.13f, 0.13f);
                    glLineWidth(1.5f);
                    RenderHelper.drawOutlinedRoundedRectangle(2, 2, component.getWidth(), component.getHeight(), 10, 0.13f, 0.13f, 0.13f, .9f, 1);
                    glLineWidth(1);
                    glColor4f(.17f, .17f, .18f, .9f);
                    RenderHelper.drawOutlinedRoundedRectangle(0, 0, component.getWidth(), component.getHeight(), 10, .17f, .17f, .18f, .9f, 1);
                }
                glColor4f(0.13f, 0.13f, 0.13f, 1);
                RenderHelper.drawHalfOutlinedRoundedRectangle(0, 0, component.getWidth(), 10, 10, 0.13f, 0.13f, 0.13f, 1, 2);
            } else if (Themes.mode.getValue() == Themes.Theme.KAMI) {
                glLineWidth(2f);
                glColor4f(.17f, .17f, .18f, .9f);
                RenderHelper.drawFilledRectangle(0, 0, component.getWidth(), component.getHeight());
                glColor3f(.59f, .05f, .11f);
                glLineWidth(1.5f);
                RenderHelper.drawRectangle(0, 0, component.getWidth(), component.getHeight());
            } else if (Themes.mode.getValue() == Themes.Theme.KAMIBLUE) {
                glLineWidth(2f);
                glColor4f(.17f, .17f, .18f, .9f);
                RenderHelper.drawFilledRectangle(0, 0, component.getWidth(), component.getHeight());
                glColor3f(.60f, .56f, 1.00f);
                glLineWidth(1.5f);
                RenderHelper.drawRectangle(0, 0, component.getWidth(), component.getHeight());
            } else if (Themes.mode.getValue() == Themes.Theme.MODERN2) {
                if (component.getHeight() != 12) {
                    glColor3f(.17f, .17f, .18f);
                    RenderHelper.drawOutlinedRoundedRectangle(0, 0, component.getWidth(), component.getHeight(), 10, .17f, .17f, .18f, 1f, 0);
                }

                glColor3f(0.05f, 0.33f, 0.8f);
                RenderHelper.drawHalfOutlinedRoundedRectangle(0, 0, component.getWidth(), 11, 10, 0.05f, 0.33f, 0.8f, 1f, 0);
            }
        }
    }
}
