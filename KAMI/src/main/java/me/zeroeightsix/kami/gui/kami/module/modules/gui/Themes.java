package me.zeroeightsix.kami.gui.kami.module.modules.gui;

import me.zeroeightsix.kami.gui.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

/**
 * Created by TBM on 13/12/2019.
 */
@Module.Info(name = "Themes", category = Module.Category.GUI, description = "Changes the GUI theme", showOnArray = false)
public class Themes extends Module {

    public static Setting<Theme> mode = Settings.e("Mode", Theme.MODERN);

    private static Themes INSTANCE = new Themes();

    public Themes() {
        INSTANCE = this;
        register(mode);
    }

    public enum Theme {
        MODERN, MODERN2, KAMI, KAMIBLUE
    }

    @Override
    public void onDisable() {
        this.enable();
    }

}
