package me.zeroeightsix.kami.module.modules.gui;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.modules.movement.ElytraFlight;
import me.zeroeightsix.kami.module.modules.render.AntiFog;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

@Module.Info(name = "Themes", category = Module.Category.GUI, description = "Changes the GUI theme")
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
}
