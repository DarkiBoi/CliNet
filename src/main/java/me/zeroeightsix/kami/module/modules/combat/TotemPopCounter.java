package me.zeroeightsix.kami.module.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.TotemPopEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.util.EntityUtil;
import me.zeroeightsix.kami.util.PlayerInfo;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.Map;

@Module.Info(name = "TotemPopCounter", description = "Counts the times your enemy pops", category = Module.Category.COMBAT)
public class TotemPopCounter extends Module {

    private HashMap<String, Integer> popList = new HashMap();

    @EventHandler
    public Listener<TotemPopEvent> totemPopEvent = new Listener<>(event -> {
        if(popList == null) {
            popList = new HashMap<>();
        }


        if(popList.get(event.getEntity().getName()) == null) {
            popList.put(event.getEntity().getName(), 1);
            Command.sendChatMessage("&d" + event.getEntity().getName() + " popped " + 1 + " totem!");
        } else if(!(popList.get(event.getEntity().getName()) == null)) {
            int popCounter = popList.get(event.getEntity().getName());
            int newPopCounter = popCounter += 1;
            popList.put(event.getEntity().getName(), newPopCounter);
            Command.sendChatMessage("&d" + event.getEntity().getName() + " popped " + newPopCounter + " totems!");
        }

    });

    @Override
    public void onUpdate() {
        for(EntityPlayer player : mc.world.playerEntities) {
            if(player.getHealth() <= 0) {
                if(popList.containsKey(player.getName())) {
                    Command.sendChatMessage("&d" + player.getName() + " died after popping " + popList.get(player.getName()) + " totems!");
                    popList.remove(player.getName(), popList.get(player.getName()));
                }
            }
        }
    }

    @EventHandler
    public Listener<PacketEvent.Receive> totemPopListener = new Listener<>(event -> {

        if (mc.world == null || mc.player == null) {
            return;
        }

        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35) {
                Entity entity = packet.getEntity(mc.world);
                KamiMod.EVENT_BUS.post(new TotemPopEvent(entity));
            }
        }

    });

}
