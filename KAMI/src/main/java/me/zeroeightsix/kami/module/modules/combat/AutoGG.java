package me.zeroeightsix.kami.module.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Darki on 08/01/2020
 *
 */

@Module.Info(name = "AutoGG", description = "Posts a message when you kill a player", category = Module.Category.COMBAT)
public class AutoGG extends Module {

    private String GG = "Buenos Dios Fuckboy";


    private ConcurrentHashMap<String, Integer> announcedEntities = new ConcurrentHashMap<>();

    @EventHandler
    private Listener<LivingDeathEvent> listener = new Listener<>(event -> {
        Entity entity = event.getEntity();

        String entityUUID = entity.getUniqueID().toString();

        System.out.println(entityUUID);

        if(announcedEntities == null) {
            announcedEntities = new ConcurrentHashMap<>();
        }

        announcedEntities.forEach((uuid, integer) ->  {
            System.out.println(uuid);
            System.out.println(integer);
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

        /*EntityPlayer player = (EntityPlayer) event.getEntity();*/

        if(ModuleManager.getModuleByName("CrystalAura").isDisabled() || ModuleManager.getModuleByName("Aura").isDisabled()) {
            return;
        }

        if(!(mc.player.getDistance(entity) < (CrystalAura.instace.hitRange.getValue() + 2))) {
            return;
        }

        addAnnouncedEntity(entityUUID);

        mc.player.connection.sendPacket(new CPacketChatMessage(sanitizeString(GG)));


    });

    @Override
    public void onUpdate() {
        announcedEntities.forEach((uuid, integer) -> {
            if(integer <= 0) {
                System.out.println(uuid + " removed");
                announcedEntities.remove(uuid);
            } else {
                announcedEntities.put(uuid, integer - 1);
                System.out.println(integer);
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

}
