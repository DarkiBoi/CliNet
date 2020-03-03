package me.zeroeightsix.kami.module.modules.render;

import com.google.common.util.concurrent.FutureCallback;
import com.mojang.authlib.GameProfile;
import joptsimple.internal.Strings;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.KamiEvent;
import me.zeroeightsix.kami.event.events.EntityConnectEvent;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11.*;

/*
 * Created by Darki
 */

@Module.Info(name = "LogoutSpots", description = "Shows you where players logged off", category = Module.Category.RENDER)
public class LogoutSpots extends Module {

    private final SimpleTimer timer = new SimpleTimer();

    private boolean ignore = false;

    private HashMap<String, Vec3d> loggedPlayers = new HashMap<>();

    public Setting<Integer> rSetting = register(Settings.integerBuilder("Red").withMinimum(0).withMaximum(255).withValue(255).build());
    public Setting<Integer> gSetting = register(Settings.integerBuilder("Green").withMinimum(0).withMaximum(255).withValue(0).build());
    public Setting<Integer> bSetting = register(Settings.integerBuilder("Blue").withMinimum(0).withMaximum(255).withValue(0).build());
    public Setting<Integer> aSetting = register(Settings.integerBuilder("Alpha").withMinimum(0).withMaximum(255).withValue(255).build());
    private Setting<Float> scale = register(Settings.floatBuilder("Scale").withMinimum(.5f).withMaximum(10f).withValue(1f).build());
    public Setting<Boolean> debugSetting = register(Settings.b("Debug", false));



    @EventHandler
    public Listener<EntityConnectEvent.Leave> leaveEvent = new Listener<>(event -> {
        if (loggedPlayers == null) {
            loggedPlayers = new HashMap<>();
        }

        if (mc.world == null) {
            return;
        }

        EntityPlayer player = mc.world.getPlayerEntityByUUID(event.getPlayerInfo().getId());
        if (player != null && mc.player != null && !mc.player.equals(player)) {
            loggedPlayers.put(player.getName(), player.getPositionVector());
            if (debugSetting.getValue()) {
                Command.sendChatMessage(player.getName() + " logged out at " + player.getPositionVector());
            }
        }

    });

    @EventHandler
    public Listener<EntityConnectEvent.Join> joinEvent = new Listener<>(event -> {
       if (loggedPlayers == null) {
           return;
       }

       if (mc.world == null) {
           return;
       }

       EntityPlayer player = mc.world.getPlayerEntityByUUID(event.getPlayerInfo().getId());

        if (player != null && mc.player != null && !mc.player.equals(player)) {
            if (loggedPlayers.containsKey(player.getName())) {
            loggedPlayers.remove(player.getName());
            Command.sendChatMessage(player.getName() + " joined after leaving!");
       }



       }






    });

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        loggedPlayers.clear();
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        for(Map.Entry<String, Vec3d> entry : loggedPlayers.entrySet()) {
            String name = entry.getKey();
            Vec3d pos = entry.getValue();
            int x = (int) Math.round(pos.x);
            int y = (int) Math.round(pos.y);
            int z = (int) Math.round(pos.z);
            String str = name + " logout spot at X: " + x + " Y: " + y + " Z: " + z;
            KamiTessellator.prepare(GL_LINE_LOOP);
            KamiTessellator.drawBoxWithVec3d(pos, rSetting.getValue(), gSetting.getValue(), bSetting.getValue(), aSetting.getValue(), GeometryMasks.Quad.ALL);
            KamiTessellator.release();
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            drawNametag(str, pos.add(0.5, 0.5, 0.5));
            GlStateManager.disableTexture2D();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister((Object) this);
        if (mc.world == null || mc.player == null) {
            return;
        }
        loggedPlayers.clear();
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register((Object) this);
        if (mc.world == null || mc.player == null) {
            return;
        }
        loggedPlayers.clear();
    }

    private void fireEvents(SPacketPlayerListItem.Action action, PlayerInfo info, GameProfile profile) {
        if (ignore || info == null) {
            return;
        }
        switch (action) {
            case ADD_PLAYER: {
                KamiMod.EVENT_BUS.post(new EntityConnectEvent.Join(info, profile));
                break;
            }
            case REMOVE_PLAYER: {
                KamiMod.EVENT_BUS.post(new EntityConnectEvent.Leave(info, profile));
                break;
            }
        }
    }

    @EventHandler
    public Listener<FMLNetworkEvent.ClientConnectedToServerEvent> onClientConnect = new Listener<>(event ->  {
        ignore = false;
    });

    @EventHandler
    public Listener<FMLNetworkEvent.ClientDisconnectionFromServerEvent> onClientDisconnect = new Listener<>(event -> {
        ignore = false;
        loggedPlayers.clear();
    });

    @EventHandler
    public Listener<PacketEvent.Receive> onPacketIn = new Listener<>(event -> {
        if (ignore && timer.isStarted() && timer.hasTimeElapsed(5000)) {
            ignore = false;
        }

        if (!ignore && event.getPacket() instanceof SPacketCustomPayload) {
            ignore = true;
            timer.start();
        } else if (ignore && event.getPacket() instanceof SPacketChunkData) {
            ignore = false;
            timer.reset();
        }
    });

    @EventHandler
    private Listener<PacketEvent.Receive> onScoreboardEvent = new Listener<>(event -> {
        if (event.getEra() == KamiEvent.Era.PRE) {
            if (event.getPacket() instanceof SPacketPlayerListItem) {
                final SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
                if (!Action.ADD_PLAYER.equals(packet.getAction())
                        && !Action.REMOVE_PLAYER.equals(packet.getAction())) {
                    return;
                }

                packet
                        .getEntries()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(
                                data ->
                                        !Strings.isNullOrEmpty(data.getProfile().getName())
                                                || data.getProfile().getId() != null)
                        .forEach(
                                data -> {
                                    final String name = data.getProfile().getName();
                                    final UUID id = data.getProfile().getId();
                                    final AtomicInteger retries = new AtomicInteger(1);
                                    PlayerInfoHelper.registerWithCallback(
                                            id,
                                            name,
                                            new FutureCallback<PlayerInfo>() {
                                                @Override
                                                public void onSuccess(@Nullable PlayerInfo result) {
                                                    fireEvents(packet.getAction(), result, data.getProfile());
                                                }

                                                @Override
                                                public void onFailure(Throwable t) {
                                                    if (retries.getAndDecrement() > 0) {
                                                        PlayerInfoHelper.registerWithCallback(
                                                                data.getProfile().getId(), name, this);
                                                    } else {
                                                        t.printStackTrace();
                                                        PlayerInfoHelper.generateOfflineWithCallback(name, this);
                                                    }
                                                }
                                            });
                                });
            }
        }
    });

    public static class SimpleTimer {

        private static final DateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss.SSS");
        private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        static {
            TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        public static String toFormattedTime(long time) {
            return TIME_FORMATTER.format(new Date(time));
        }

        public static String toFormattedDate(long time) {
            return DATE_FORMATTER.format(new Date(time));
        }

        private long timeStarted = -1;
        private long timeStopped = -1;

        public SimpleTimer(boolean startOnInit) {
            if (startOnInit) {
                start();
            }
        }

        public SimpleTimer() {
            this(false);
        }

        public void start() {
            timeStarted = System.currentTimeMillis();
        }

        public void stop() {
            timeStopped = System.currentTimeMillis();
        }

        public void reset() {
            timeStarted = timeStopped = -1;
        }

        public boolean isStarted() {
            return timeStarted > -1;
        }

        public boolean isStopped() {
            return timeStopped > -1;
        }

        public long getTimeStarted() {
            return Math.max(timeStarted, 0L);
        }

        public long getTimeStopped() {
            return Math.max(timeStopped, 0L);
        }

        private long _time() {
            return isStopped() ? getTimeStopped() : System.currentTimeMillis();
        }

        public long getTimeElapsed() {
            return _time() - getTimeStarted();
        }

        public boolean hasTimeElapsed(long time) {
            return time <= getTimeElapsed();
        }

        public String getFormattedTimeStarted() {
            return DATE_FORMATTER.format(new Date(getTimeStarted()));
        }

        public String getFormattedTimeStopped() {
            return DATE_FORMATTER.format(new Date(getTimeStopped()));
        }

        public String getFormattedTimeElapsed() {
            return formatInterval(Math.max(getTimeElapsed(), 0));
        }

        protected static String formatInterval(long delta) {
            final long hr = TimeUnit.MILLISECONDS.toHours(delta);
            final long min = TimeUnit.MILLISECONDS.toMinutes(delta - TimeUnit.HOURS.toMillis(hr));
            final long sec = TimeUnit.MILLISECONDS
                    .toSeconds(delta - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
            final long ms = TimeUnit.MILLISECONDS.toMillis(
                    delta - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS
                            .toMillis(sec));
            return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
        }
    }

    public void drawNametag(String str, Vec3d vec) {
        GlStateManager.pushMatrix();

        Vec3d interp = EntityUtil.getInterpolatedRenderPos(vec);
        float yAdd = 2;
        double x = interp.x;
        double y = interp.y + yAdd;
        double z = interp.z;

        float viewerYaw = mc.getRenderManager().playerViewY;
        float viewerPitch = mc.getRenderManager().playerViewX;
        boolean isThirdPersonFrontal = mc.getRenderManager().options.thirdPersonView == 2;
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);

        float f = (float) mc.player.getDistance(vec.x, vec.y, vec.z);
        float m = (f / 8f) * (float) (Math.pow(1.2589254f, this.scale.getValue()));
        GlStateManager.scale(m, m, m);

        FontRenderer fontRendererIn = mc.fontRenderer;
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);

        int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder bufferbuilder = tessellator.getBuffer();

        glTranslatef(0, -20, 0);
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(-i - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        bufferbuilder.pos(-i - 1, 19, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        bufferbuilder.pos(i + 1, 19, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        bufferbuilder.pos(i + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
        tessellator.draw();

        bufferbuilder.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(-i - 1, 8, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
        bufferbuilder.pos(-i - 1, 19, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
        bufferbuilder.pos(i + 1, 19, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
        bufferbuilder.pos(i + 1, 8, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        fontRendererIn.drawString(str, -i, 10, 0xffffff);
        GlStateManager.glNormal3f(0.0F, 0.0F, 0.0F);
        GlStateManager.popMatrix();

    }




}
