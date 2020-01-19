package me.zeroeightsix.kami.module.modules.movement;

import io.netty.util.internal.MathUtil;
import jdk.nashorn.internal.ir.Block;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.InputUpdateEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seth 26/4/2019 @ 1:56 PM.
 *
 * https://github.com/seppukudevelopment/seppuku/blob/master/src/main/java/me/rigamortis/seppuku/impl/module/movement/FlightModule.java
 */

@Module.Info(name = "SeppukuFly", description = "Makes you fly (Made by Seth)", category = Module.Category.MOVEMENT)
public class SeppukuFly extends Module {

    public final Setting<Float> speed = register(Settings.floatBuilder("Speed").withValue(0.1f).withMaximum(5.0f).withMinimum(0.0f).build());

    public final Setting<Boolean> noKick = register(Settings.b("NoKick", true));
    //public final Setting<Boolean> antiFlyDeath = register(Settings.b("AntiFlyDeath", true));

    //private List<Module> modules = new ArrayList<>();
    private int teleportId;
    private List<CPacketPlayer> packets = new ArrayList<>();

    @Override
    public void onEnable() {
        if (mc.world != null) {
            this.teleportId = 0;
            this.packets.clear();
            final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX, 0, mc.player.posZ, mc.player.onGround);
            this.packets.add(bounds);
            mc.player.connection.sendPacket(bounds);
        }
        /*
        if (antiFlyDeath.getValue()) {
            if (ModuleManager.isModuleEnabled("AntiHunger")) {
                ModuleManager.getModuleByName("AntiHunger").disable();
                modules.add(ModuleManager.getModuleByName("AntiHunger"));
            }
            if (ModuleManager.isModuleEnabled("NoFall")) {
                ModuleManager.getModuleByName("NoFall").disable();
                modules.add(ModuleManager.getModuleByName("NoFall"));
            }
        }

         */
    }

    /*
    @Override
    public void onDisable() {
        modules.forEach(Module::enable);
    }

     */

    @EventHandler
    public Listener<InputUpdateEvent> listener = new Listener<>(event -> {
            if (this.teleportId <= 0) {
                final CPacketPlayer bounds = new CPacketPlayer.Position(Minecraft.getMinecraft().player.posX, 0, Minecraft.getMinecraft().player.posZ, Minecraft.getMinecraft().player.onGround);
                this.packets.add(bounds);
                Minecraft.getMinecraft().player.connection.sendPacket(bounds);
                return;
            }

            mc.player.setVelocity(0, 0, 0);

            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625d, 0, -0.0625d)).isEmpty()) {
                double ySpeed = 0;

                if (mc.gameSettings.keyBindJump.isKeyDown()) {

                    if (this.noKick.getValue()) {
                        ySpeed = mc.player.ticksExisted % 20 == 0 ? -0.04f : 0.062f;
                    } else {
                        ySpeed = 0.062f;
                    }
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    ySpeed = -0.062d;
                } else {
                    ySpeed = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625d, -0.0625d, -0.0625d)).isEmpty() ? (mc.player.ticksExisted % 4 == 0) ? (this.noKick.getValue() ? -0.04f : 0.0f) : 0.0f : 0.0f;
                }

                final double[] directionalSpeed = BlockInteractionHelper.directionSpeed(this.speed.getValue());

                if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
                    if (directionalSpeed[0] != 0.0d || ySpeed != 0.0d || directionalSpeed[1] != 0.0d) {
                        if (mc.player.movementInput.jump && (mc.player.moveStrafing != 0 || mc.player.moveForward != 0)) {
                            mc.player.setVelocity(0, 0, 0);
                            move(0, 0, 0);
                            for (int i = 0; i <= 3; i++) {
                                mc.player.setVelocity(0, ySpeed * i, 0);
                                move(0, ySpeed * i, 0);
                            }
                        } else {
                            if (mc.player.movementInput.jump) {
                                mc.player.setVelocity(0, 0, 0);
                                move(0, 0, 0);
                                for (int i = 0; i <= 3; i++) {
                                    mc.player.setVelocity(0, ySpeed * i, 0);
                                    move(0, ySpeed * i, 0);
                                }
                            } else {
                                for (int i = 0; i <= 2; i++) {
                                    mc.player.setVelocity(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
                                    move(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
                                }
                            }
                        }
                    }
                } else {
                    if (this.noKick.getValue()) {
                        if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625d, -0.0625d, -0.0625d)).isEmpty()) {
                            mc.player.setVelocity(0, (mc.player.ticksExisted % 2 == 0) ? 0.04f : -0.04f, 0);
                            move(0, (mc.player.ticksExisted % 2 == 0) ? 0.04f : -0.04f, 0);
                        }
                    }
                }
            }
        });


    private void move(double x, double y, double z) {
        final Minecraft mc = Minecraft.getMinecraft();
        final CPacketPlayer pos = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround);
        this.packets.add(pos);
        mc.player.connection.sendPacket(pos);

        final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX + x, 0, mc.player.posZ + z, mc.player.onGround);
        this.packets.add(bounds);
        mc.player.connection.sendPacket(bounds);

        this.teleportId++;
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId - 1));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId + 1));
    }

    @EventHandler
    public Listener<PacketEvent.Send> sendListener = new Listener<>(event -> {
                if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) {
                    event.cancel();
                }
                if (event.getPacket() instanceof CPacketPlayer) {
                    final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                    if (packets.contains(packet)) {
                        packets.remove(packet);
                        return;
                    }
                    event.cancel();
                }
    });

    @EventHandler
    public Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            if (Minecraft.getMinecraft().player.isEntityAlive() && Minecraft.getMinecraft().world.isBlockLoaded(new BlockPos(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ)) && !(Minecraft.getMinecraft().currentScreen instanceof GuiDownloadTerrain)) {
                if (this.teleportId <= 0) {
                    this.teleportId = packet.getTeleportId();
                } else {
                    event.cancel();
                }
            }
        }
    });

}
