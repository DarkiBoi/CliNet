package me.zeroeightsix.kami.module.modules.player;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;

/*
* Created by Hamburger on 13/01/2020
*
* FastBow Author Seth 4/30/2019
*
* https://github.com/seppukudevelopment/seppuku/blob/558636579c2c2df5941525d9af1f6e5a4ef658cc/src/main/java/me/rigamortis/seppuku/impl/module/combat/FastBowModule.java
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
            }

        }

        if(fastBow.getValue()) {
            if (mc.player.inventory.getCurrentItem().getItem() instanceof net.minecraft.item.ItemBow &&
                    mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
                mc.player.stopActiveHand();
            }

        }

        if(fastPlace.getValue()) {
            if(main instanceof ItemBlock | off instanceof ItemBlock) {
                mc.rightClickDelayTimer = 0;
            }
        }


        else if(main instanceof ItemFood | off instanceof  ItemFood) {
            mc.rightClickDelayTimer = 0;
        }

        else {
            mc.rightClickDelayTimer = 6;
        }


    }

}