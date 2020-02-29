package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;


/*
 * Created by Hamburger 29/02/2020
 */
@Module.Info(name = "VisualRange", description = "Reports Players in VisualRange", category = Module.Category.MISC)
public class VisualRange extends Module {

    private List<String> peopleinarea;
    private List<String> peoplenearbynew;
    private List<String> peopletoremove;

    @Override
    public void onEnable() {
        peopleinarea = new ArrayList<>();
        peopletoremove = new ArrayList<>();
        }

    @Override
    public void onUpdate() {
        if (mc.world == null | mc.player == null) return;

        peoplenearbynew = new ArrayList<>();
        List<EntityPlayer> playerEntities = mc.world.playerEntities;

        for (Entity e : playerEntities) {
            if (e.getName().equals(mc.player.getName())) continue;
            peoplenearbynew.add(e.getName());
        }

        if (peoplenearbynew.size() > 0) {
            for (String name : peoplenearbynew) {
                if (!peopleinarea.contains(name)) {
                    Command.sendChatMessage(name + "  just entered visual range!");
                    peopleinarea.add(name);
                }
            }
        }

        if (peopleinarea.size() > 0) {
            for (String name : peopleinarea) {
                if (!peoplenearbynew.contains(name)) {
                    peopletoremove.add(name);
                    Command.sendChatMessage(name + " just left visual range!");

                }

            }

            if (peopletoremove.size() > 0) {
                for (String name : peopletoremove) {
                    peopleinarea.remove(name);
                }
                peopletoremove.clear();
            }

        }

    }

}

