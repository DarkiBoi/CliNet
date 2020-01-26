package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import java.util.Collection;

/*
 * Created by Hamburger on 19/01/2020
 */


@Module.Info(name = "StrengthDetect", category = Module.Category.COMBAT, description = "detects Strength usage")
public class StrengthDetect extends Module {

//        private Setting<Float> frequency = register(Settings.floatBuilder("Frequency").withMinimum(0f).withMaximum(60f).withValue(30f));
//        private long waittime = 0;
//        private Boolean hasrun = false;
//
//    @Override
//    public void onUpdate() {
//
//        if (hasrun == false) {
//            Minecraft.getMinecraft().world.loadedEntityList.stream()
//                    .filter(entity -> (entity instanceof EntityPlayer))
//                    .forEach(entity -> {
//                        EntityPlayer player = (EntityPlayer) entity;
//                        Collection<PotionEffect> effects = player.getActivePotionEffects();
//                        if (player.isPotionActive(MobEffects.STRENGTH)) {
//                            String strplayer = player.getName();
//                            if (!strplayer.equals(mc.player.getName())) {
//                                Command.sendChatMessage(strplayer + " is using strength!");
//                                //todo rework this code mess to something that looks better
//                                hasrun = true;
//                            }
//                        }
//
//                    });
//        }
//        float secondsf = frequency.getValue();
//        int seconds = (int)secondsf;
//
//        if (System.currentTimeMillis() - waittime >= seconds * 1000) {
//            hasrun = false;
//            waittime = System.currentTimeMillis();
//        }
//
//    }

}
