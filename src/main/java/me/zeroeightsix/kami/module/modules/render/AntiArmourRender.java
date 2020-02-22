package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.inventory.EntityEquipmentSlot;

@Module.Info(name="AntiArmourRender", category = Module.Category.RENDER)
public class AntiArmourRender extends Module {

    public Setting<Boolean> player = register(Settings.b("Render On Players", true));
    public Setting<Boolean> armourstand = register(Settings.b("Render On Armour Stands", true));
    public Setting<Boolean> mobs = register(Settings.b("Render On Mobs", true));

    public Setting<Boolean> helmet = register(Settings.b("Render Helmet", true));
    public Setting<Boolean> chestplate = register(Settings.b("Render Chestplate", true));
    public Setting<Boolean> leggins = register(Settings.b("Render Leggings", true));
    public Setting<Boolean> boots = register(Settings.b("Render Boots", true));

    public static AntiArmourRender INSTANCE;

    public AntiArmourRender() {
        AntiArmourRender.INSTANCE = this;
    }

    public static boolean shouldRenderPiece(EntityEquipmentSlot slotIn) {
        if (slotIn == EntityEquipmentSlot.HEAD && AntiArmourRender.INSTANCE.helmet.getValue()) {
            return true;
        } else if (slotIn == EntityEquipmentSlot.CHEST && AntiArmourRender.INSTANCE.chestplate.getValue()) {
            return true;
        } else if (slotIn == EntityEquipmentSlot.LEGS && AntiArmourRender.INSTANCE.leggins.getValue()) {
            return true;
        } else if (slotIn == EntityEquipmentSlot.FEET && AntiArmourRender.INSTANCE.boots.getValue()) {
            return true;
        } else {
            return false;
        }
    }

}