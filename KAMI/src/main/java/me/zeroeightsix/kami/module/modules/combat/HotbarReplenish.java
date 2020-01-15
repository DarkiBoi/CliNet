package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.Module;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Module.Info(name = "HotbarReplenish", description = "Replenishes on items if you're low", category = Module.Category.COMBAT)
public class HotbarReplenish extends Module {

    @Override
    public void onUpdate() {
        Map<Integer, ItemStack> hotbar = getHotbar();
        Map<Integer, ItemStack> inventory = getInventory();
        for (ItemStack item : hotbar.values()) {
            if(item.getCount() < 10) {
                inventory.forEach((index, itemStackCurrent) -> {
                    if(itemStackCurrent.equals(item)) {
                        mc.playerController.windowClick(0, index < 9 ? index + 36 : index, 0, ClickType.PICKUP, mc.player);
                    }
                });
            }
        }

    }

    private static Map<Integer, ItemStack> getInventory() {
        return getInventorySlotsRange(9, 35);
    }


    private static Map<Integer, ItemStack> getHotbar() {
        return getInventorySlotsRange(36, 44);
    }

    private static Map<Integer, ItemStack> getInventorySlotsRange(int first, int last) {

        Map<Integer, ItemStack> inventorySlots = new HashMap<>();

        for(int i = first; i < last; i++) {
            inventorySlots.put(i, mc.player.inventoryContainer.getInventory().get(i));
        }

        return inventorySlots;

    }




}
