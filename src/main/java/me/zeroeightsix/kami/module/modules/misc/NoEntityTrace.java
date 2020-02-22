package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumHand;

/**
 * Created by 086 on 8/04/2018.
 */
@Module.Info(name = "NoEntityTrace", category = Module.Category.MISC, description = "Blocks entities from stopping you from mining")
public class NoEntityTrace extends Module {

    private Setting<TraceMode> mode = register(Settings.e("Mode", TraceMode.DYNAMIC));
    private Setting<Boolean> pickaxe = register(Settings.b("Pickaxe", true));

    private static NoEntityTrace INSTANCE;

    public NoEntityTrace() {
        NoEntityTrace.INSTANCE = this;
    }

    public static boolean shouldBlock() {
        if (INSTANCE.isEnabled()) {
            if (INSTANCE.pickaxe.getValue()) {
                if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemPickaxe) {
                    return true;
                } else {
                    return INSTANCE.mode.getValue() == TraceMode.STATIC || mc.playerController.isHittingBlock;
                }
            }
            return INSTANCE.mode.getValue() == TraceMode.STATIC || mc.playerController.isHittingBlock;
        }
        return false;
    }

    private enum TraceMode {
        STATIC, DYNAMIC
    }
}
