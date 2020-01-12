package me.zeroeightsix.kami.module.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Darki on 08/01/2020
 * With help of Gods 086 and hub
 */

@Module.Info(name = "AutoGG", description = "Posts a message when you kill a player", category = Module.Category.COMBAT)
public class AutoGG extends Module {

    public static Setting<Mode> mode = Settings.e("Mode", Mode.CLINET);

    private final String CLINET1 = "Good Fight ";
    private final String CLINET2 = "! CliNet owns me and all!";
    private final String TOXIC = "EZZZZZZZ ";
    private final String PLIVID = "Buenos Dios Fuckboy";
    private final String DUTCHERINO1 = "GG volgende keer beter";
    private final String DUTCHERINO2 = "GG Goed Gevecht ";
    private final String DUTCHERINO21 = "! CliNet beheert mij en iedereen!";
    private final String DUTCHERINO3 = "GG! Goed Gevecht ";
    private final String DUTCHERINO31 = ", maar niet goed genoeg";
    private final String DUTCHERINO4 = "GG beetje jammer dat je hebt verloren!";


    public enum Mode {
        CLINET, TOXIC, PLIVID, DUTCHERINO, RANDOM
    }

    public AutoGG() {
        register(mode);
    }


    private ConcurrentHashMap<String, Integer> announcedEntities = new ConcurrentHashMap<>();

    @EventHandler
    private Listener<LivingDeathEvent> listener = new Listener<>(event -> {
        Entity entity = event.getEntity();

        String entityUUID = entity.getUniqueID().toString();

        Entity damageSource = event.getSource().getImmediateSource();

        if(event.getSource().damageType.equals("generic")) {
            return;
        }

        if(Friends.isFriend(event.getEntity().getName())) {
            return;
        }

        if(damageSource == null) {
            return;
        }

        if(!(damageSource.getName().equals(mc.player.getName()))) {
            return;
        }

        if(announcedEntities == null) {
            announcedEntities = new ConcurrentHashMap<>();
        }

        announcedEntities.forEach((uuid, integer) ->  {
            if(uuid.toString().equals(entityUUID.toString())) {
                return;
            }
        });

        if(mc.player == null) {
            return;
        }

        if(entity == null) {
            return;
        }

        /*if(entity == null) {
            return;
        }*/

        /*if(!(EntityUtil.isPlayer(event.getEntity()))) {
            return;
        }*/

        if(entity == mc.player) {
            return;
        }

        /*if(!(event.getSource().getImmediateSource().equals(mc.player))) {
            mc.player.sendChatMessage("IM NOT THE SOURCE NIGGA");
            return;
        }*/

        if(ModuleManager.getModuleByName("CrystalAura").isDisabled() || ModuleManager.getModuleByName("Aura").isDisabled()) {
            return;
        }



        if(!(mc.player.getDistance(entity) < (CrystalAura.instace.hitRange.getValue() + 2))) {
            return;
        }

        addAnnouncedEntity(entityUUID);

        //TODO rework this mess
        switch (mode.getValue()) {
            case CLINET:
                postGG(CLINET1 + entity.getName() + CLINET2);
                break;
            case TOXIC:
                postGG(TOXIC + entity.getName());
                break;
            case PLIVID:
                postGG(PLIVID);
                break;
            case DUTCHERINO:
                Random r = new Random();
                int n = r.nextInt(4);
                switch (n += 1) {
                    case 1:
                        postGG(DUTCHERINO1);
                        break;
                    case 2:
                        postGG(DUTCHERINO2 + entity.getName() + DUTCHERINO21);
                        break;
                    case 3:
                        postGG(DUTCHERINO3 + entity.getName() + DUTCHERINO31);
                        break;
                    case 4:
                        postGG(DUTCHERINO4);
                }
                break;
            case RANDOM:
                String[] gglist = {CLINET1, DUTCHERINO1 , TOXIC, DUTCHERINO2, DUTCHERINO3, DUTCHERINO4};
                Random random = new Random();
                String randomgg = gglist[random.nextInt(gglist.length)];
               if(randomgg == DUTCHERINO2){randomgg = DUTCHERINO2 + entity.getName() + DUTCHERINO21;}
               if(randomgg == DUTCHERINO3){randomgg = DUTCHERINO3 + entity.getName() + DUTCHERINO31;}
               postGG(randomgg);


        }


    });

    @Override
    public void onUpdate() {
        announcedEntities.forEach((uuid, integer) -> {
            if(integer <= 0) {
            } else {
                announcedEntities.put(uuid, integer - 1);
            }
        });
    }

    private void addAnnouncedEntity(String inputUuid) {

        if(announcedEntities == null) {
            announcedEntities = new ConcurrentHashMap<>();
        }

        announcedEntities.put(inputUuid, 20);

    }

    @Override
    public void onEnable() {
        announcedEntities = new ConcurrentHashMap<>();
    }

    @Override
    public void onDisable() {
        announcedEntities = null;
    }

    private String sanitizeString(String string) {
        String sanitizedString = string.replaceAll("\u00A7", "");

        if (sanitizedString.length() > 255) {
            sanitizedString = sanitizedString.substring(0, 255);
        }

        return sanitizedString;
    }

    public void postGG(String text) {
        mc.player.connection.sendPacket(new CPacketChatMessage(sanitizeString(text)));
    }

}
