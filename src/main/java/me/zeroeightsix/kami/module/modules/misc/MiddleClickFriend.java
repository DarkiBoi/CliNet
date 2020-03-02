package me.zeroeightsix.kami.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.command.commands.FriendCommand;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.util.Friends;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

/*
 * Created by Hamburger 22/02/2020
 */

@Module.Info(name = "MiddleClickFriend", description = "Middle click people to friend", category = Module.Category.MISC)
public class MiddleClickFriend extends Module {

    @EventHandler
    public Listener<InputEvent.MouseInputEvent> listener = new Listener<>(event -> {
            if(mc.world == null) return;
            if (Mouse.isButtonDown(2)) {
                RayTraceResult result = mc.objectMouseOver;
                if(result.typeOfHit == RayTraceResult.Type.ENTITY){
                    if(result.entityHit instanceof EntityPlayer){
                        String name = result.entityHit.getName();
                        Friends.Friend f = FriendCommand.getFriendByName(name);
                        if (f == null) {
                            Command.sendChatMessage("Failed to find UUID of " + name);
                            return;
                        }
                        if(Friends.isFriend(name)) {
                            Friends.friends.getValue().remove(f);
                            Command.sendChatMessage("&b" + name + " has been unfriended");
                        }
                        else {

                            Friends.friends.getValue().add(f);
                            Command.sendChatMessage("&b" + name + " has been friended");
                        }

                    }
                }
            }

        });

}