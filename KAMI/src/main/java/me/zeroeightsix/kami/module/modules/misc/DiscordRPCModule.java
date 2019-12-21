package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.DiscordPresence;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;

@Module.Info(name = "DiscordRPC", category = Module.Category.MISC, description = "Discord Rich Presence")
public class DiscordRPCModule extends Module {

    public Setting<Boolean> startupGlobal = register(Settings.b("Enable Automatically", false));
    public Setting<Boolean> versionGlobal = register(Settings.b("Version", true));
    public Setting<Boolean> usernameGlobal = register(Settings.b("Username", true));
    public Setting<Boolean> hpGlobal = register(Settings.b("Health", true));
    public Setting<Boolean> ipGlobal = register(Settings.b("Server IP", true));

    @Override
    public void onEnable() {
        DiscordPresence.start();
    }

    public void onDisable() { DiscordPresence.disable(); }
}
