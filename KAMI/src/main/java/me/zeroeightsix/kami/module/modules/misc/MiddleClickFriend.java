package me.zeroeightsix.kami.module.modules.misc;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.command.commands.FriendCommand;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.gui.rgui.component.listen.MouseListener;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.util.Friends;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;
import scala.swing.event.MouseButtonEvent;
import scala.swing.event.MouseClicked;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


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
                        if(Friends.isFriend(name)) Command.sendChatMessage("This person has already been friended");
                        else {
                            Friends.Friend f = FriendCommand.getFriendByName(name);
                            if (f == null) {
                                Command.sendChatMessage("Failed to find UUID of " + name);
                                return;
                            }
                            Friends.friends.getValue().add(f);
                            Command.sendChatMessage("&b" + name + " has been friended");
                        }

                    }
                }
            }

        });

}