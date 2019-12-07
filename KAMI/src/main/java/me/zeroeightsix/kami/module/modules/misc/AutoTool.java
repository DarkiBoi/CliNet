package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.module.Module;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Created by 086 on 2/10/2018.
 */
@Module.Info(name = "AutoTool", description = "Automatically switch to the best tools when mining or attacking", category = Module.Category.MISC)
public class AutoTool extends Module {

    @EventHandler
    private Listener<PlayerInteractEvent.LeftClickBlock> leftClickListener = new Listener<>(event -> {
        equipBestTool(mc.world.getBlockState(event.getPos()));
    });

    @EventHandler
    private Listener<AttackEntityEvent> attackListener = new Listener<>(event -> {
        equipBestWeapon();
    });

    private void equipBestTool(IBlockState blockState) {
        int bestSlot = -1;
        double max = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty) continue;
            float speed = stack.getDestroySpeed(blockState);
            int eff;
            if (speed > 1) {
                speed += ((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0 ? (Math.pow(eff, 2) + 1) : 0);
                if (speed > max) {
                    max = speed;
                    bestSlot = i;
                }
            }
        }
        if (bestSlot != -1) equip(bestSlot);
    }

    public static void equipBestWeapon() {
        int bestSlot = -1;
        double maxDamage = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty) continue;
            if (stack.getItem() instanceof ItemTool) {
                double damage = (((ItemTool) stack.getItem()).attackDamage + (double) EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED));
                if (damage > maxDamage) {
                    maxDamage = damage;
                    bestSlot = i;
                }
            } else if (stack.getItem() instanceof ItemSword) {
                double damage = (((ItemSword) stack.getItem()).getAttackDamage() + (double) EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED));
                if (damage > maxDamage) {
                    maxDamage = damage;
                    bestSlot = i;
                }
            }
        }
        if (bestSlot != -1) equip(bestSlot);
    }

    private static void equip(int slot) {
        mc.player.inventory.currentItem = slot;
        mc.playerController.syncCurrentPlayItem();
    }

}
