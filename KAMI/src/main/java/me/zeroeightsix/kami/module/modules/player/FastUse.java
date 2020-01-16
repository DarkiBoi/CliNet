package me.zeroeightsix.kami.module.modules.player;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemExpBottle;

/*
* Created by Hamburger on 13/01/2020
*/

@Module.Info(name = "FastUse", category = Module.Category.PLAYER, description = "Makes you use stuff fast")

public class FastUse extends Module {


    private Setting<Boolean> fastXP = register(Settings.b("FastXP", true));
    private Setting<Boolean> fastBow = register(Settings.b("FastBow", false));
    private Setting<Boolean> fastPlace = register(Settings.b("FastPlace", false));


    @Override
    public void onUpdate() {

        Item main = mc.player.getHeldItemMainhand().getItem();
        Item off = mc.player.getHeldItemOffhand().getItem();

        if(fastXP.getValue()) {
            if(main instanceof ItemExpBottle | off instanceof ItemExpBottle) {
                mc.rightClickDelayTimer = 0;
                return;
            }
        }

        if(fastBow.getValue()) {
            if(main instanceof ItemBow | off instanceof ItemBow) {
                mc.rightClickDelayTimer = 0;
                return;
            }
        }

        if(fastPlace.getValue()) {
            if(main instanceof ItemBlock | off instanceof ItemBlock) {
                mc.rightClickDelayTimer = 0;
                return;
            }
        }

        else {
            mc.rightClickDelayTimer = 4;
        }


    }

}