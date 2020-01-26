package me.zeroeightsix.kami.module.modules.render;

import com.google.common.util.concurrent.FutureCallback;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.util.*;
import com.mojang.authlib.GameProfile;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import joptsimple.internal.Strings;
import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.event.KamiEvent;
import me.zeroeightsix.kami.event.events.EntityConnectEvent;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Module.Info(name = "LogoutSpots", description = "Shows you where players logged off", category = Module.Category.RENDER)
public class LogoutSpots extends Module {

    private final SimpleTimer timer = new SimpleTimer();

    private boolean ignore = false;

    private HashMap<String, Vec3d> loggedPlayers = new HashMap<>();

    public Setting<Integer> rSetting = register(Settings.integerBuilder("Red").withMinimum(0).withMaximum(255).withValue(255).build());
    public Setting<Integer> gSetting = register(Settings.integerBuilder("Green").withMinimum(0).withMaximum(255).withValue(0).build());
    public Setting<Integer> bSetting = register(Settings.integerBuilder("Blue").withMinimum(0).withMaximum(255).withValue(0).build());
    public Setting<Integer> aSetting = register(Settings.integerBuilder("Alpha").withMinimum(0).withMaximum(255).withValue(255).build());


    @EventHandler
    public Listener<EntityConnectEvent.Leave> leaveEvent = new Listener<>(event -> {
        if(loggedPlayers == null) {
            loggedPlayers = new HashMap<>();
        }

        if(mc.world == null) {
            return;
        }

        EntityPlayer player = mc.world.getPlayerEntityByUUID(event.getPlayerInfo().getId());
        if (player != null && mc.player != null && !mc.player.equals(player)) {
            loggedPlayers.put(player.getName(), player.getPositionVector());
            Command.sendChatMessage(player.getName() + " logged out at " + player.getPositionVector());
        }

    });

    @Override
    public void onWorldRender(RenderEvent event) {
        KamiTessellator.prepareOutlines(10F);

        for(Map.Entry<String, Vec3d> entry : loggedPlayers.entrySet()) {
            String name = entry.getKey();
            Vec3d pos = entry.getValue();

            KamiTessellator.drawBoxWithVec3d(entry.getValue(), rSetting.getValue(), gSetting.getValue(), bSetting.getValue(), aSetting.getValue(), GeometryMasks.Quad.ALL);

        }

        KamiTessellator.release();
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




}
