package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.Module;
import net.minecraft.item.Item;
import net.minecraft.item.ItemExpBottle;

/*
* Created by Hamburger on 13/01/2020
*/

@Module.Info(name = "FastEXP", category = Module.Category.COMBAT, description = "EXP bottles go fast")

public class FastEXP extends Module {



    @Override
    public void onUpdate() {

        Item main = mc.player.getHeldItemMainhand().getItem();
        Item off = mc.player.getHeldItemOffhand().getItem();

        if(main instanceof ItemExpBottle | off instanceof ItemExpBottle)
        {
           mc.rightClickDelayTimer = 0;
        }

    }
    
}