package me.zeroeightsix.kami;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.misc.DiscordRPCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.common.FMLLog;

import static me.zeroeightsix.kami.KamiMod.MODVER;

public class DiscordPresence {
    private static final String APP_ID = "657282743117545472";
    private static final DiscordRPC rpc;
    public static DiscordRichPresence presence;
    private static boolean hasStarted;
    public static final Minecraft mc = Minecraft.getMinecraft();
    private static String details;
    private static String state;
    private static int players;
    private static int maxPlayers;
    private static ServerData svr;
    private static String[] popInfo;
    private static int players2;
    private static int maxPlayers2;

    public static void disable() {
        DiscordPresence.presence.state = "";
    }

    public static void start() {
        boolean versionPrivateStart = ((DiscordRPCModule) ModuleManager.getModuleByName("DiscordRPC")).versionGlobal.getValue();

        FMLLog.log.info("Starting Discord RPC");
        if (DiscordPresence.hasStarted) {
            return;
        }
        DiscordPresence.hasStarted = true;
        final DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.disconnected = ((var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + String.valueOf(var1) + ", var2: " + var2));
        DiscordPresence.rpc.Discord_Initialize(APP_ID, handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        if (versionPrivateStart) {
            DiscordPresence.presence.details = MODVER + " - Looking at Main Menu";
        }
        else {
            DiscordPresence.presence.details = "Main Menu";
        }
        DiscordPresence.presence.state = "";
        DiscordPresence.presence.largeImageKey = "kami";
        DiscordPresence.presence.largeImageText = "CliNet";

        DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                boolean versionPrivate = ((DiscordRPCModule) ModuleManager.getModuleByName("DiscordRPC")).versionGlobal.getValue();
                boolean ipPrivate = ((DiscordRPCModule) ModuleManager.getModuleByName("DiscordRPC")).ipGlobal.getValue();
                boolean hpPrivate = ((DiscordRPCModule) ModuleManager.getModuleByName("DiscordRPC")).hpGlobal.getValue();
                boolean userNamePrivate = ((DiscordRPCModule) ModuleManager.getModuleByName("DiscordRPC")).usernameGlobal.getValue();
                boolean coordsPrivate = ((DiscordRPCModule) ModuleManager.getModuleByName("DiscordRPC")).usernameGlobal.getValue();

                try {
                    DiscordPresence.rpc.Discord_RunCallbacks();
                    details = "";
                    state = "";
                    players = 0;
                    maxPlayers = 0;
                    if (mc.isIntegratedServerRunning()) {
                        if (versionPrivate) {
                            details = MODVER + " - Playing Singleplayer";
                        }
                        else {
                            details = "Singleplayer";
                        }
                    }
                    else if (mc.getCurrentServerData() != null) {
                        svr = mc.getCurrentServerData();
                        if (!svr.serverIP.equals("")) {
                            if (userNamePrivate) {
                                if (versionPrivate) {
                                    details = MODVER + " - Playing as " + mc.player.getName();
                                } else {
                                    details = mc.player.getName();
                                }
                            }
                            else {
                                if (versionPrivate) {
                                    details = MODVER + " - Playing Multiplayer";
                                } else {
                                    details = "Multiplayer";
                                }
                            }
                            if (ipPrivate) {
                                if (hpPrivate) {
                                    state = svr.serverIP + " (" + ((int) mc.player.getHealth() / 2) + " hearts)";
                                } else if (coordsPrivate) {
                                    state = svr.serverIP + " (" + ((int) mc.player.posX) + ", " + ((int) mc.player.posY) + ", " + ((int) mc.player.posZ);
                                } else if (coordsPrivate && hpPrivate) {
                                    state = svr.serverIP + " (" + ((int) mc.player.getHealth() / 2) + " hearts) " + " (" + ((int) mc.player.posX) + ", " + ((int) mc.player.posY) + ", " + ((int) mc.player.posZ);
                                }
                                else {
                                    state = svr.serverIP;
                                }
                                if (svr.populationInfo != null) {
                                    popInfo = svr.populationInfo.split("/");
                                    if (popInfo.length > 2) {
                                        players2 = Integer.parseInt(popInfo[0]);
                                        maxPlayers2 = Integer.parseInt(popInfo[1]);
                                    }
                                }
                            }
                            else {
                                if (hpPrivate) {
                                    state = "(" + ((int) mc.player.getHealth() / 2) + " hearts)";
                                }
                                else {
                                    state = "";
                                }
                            }
                        }
                    }
                    else {
                        if (versionPrivate) {
                            details = MODVER + " - Looking at Main Menu";
                        }
                        else {
                            details = "Main Menu";
                        }
                        state = "";
                    }
                    if (!details.equals(DiscordPresence.presence.details) || !state.equals(DiscordPresence.presence.state)) {
                        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
                    }
                    DiscordPresence.presence.details = details;
                    DiscordPresence.presence.state = state;
                    DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException e3) {
                    e3.printStackTrace();
                }
            }
            return;
        }, "Discord-RPC-Callback-Handler").start();
        FMLLog.log.info("Discord RPC initialised succesfully");
    }

    private static /* synthetic */ void lambdastart1() {
        while (!Thread.currentThread().isInterrupted()) {
            boolean versionPrivate = ((DiscordRPCModule) ModuleManager.getModuleByName("DiscordRPC")).versionGlobal.getValue();
            boolean ipPrivate = ((DiscordRPCModule) ModuleManager.getModuleByName("DiscordRPC")).ipGlobal.getValue();
            boolean hpPrivate = ((DiscordRPCModule) ModuleManager.getModuleByName("DiscordRPC")).hpGlobal.getValue();
            boolean userNamePrivate = ((DiscordRPCModule) ModuleManager.getModuleByName("DiscordRPC")).usernameGlobal.getValue();

            try {
                DiscordPresence.rpc.Discord_RunCallbacks();
                String details = "";
                String state = "";
                int players = 0;
                int maxPlayers = 0;
                if (mc.isIntegratedServerRunning()) {
                    if (versionPrivate) {
                        details = MODVER + " - Playing Singleplayer";
                    }
                    else {
                        details = "Singleplayer";
                    }
                }
                else if (mc.getCurrentServerData() != null) {
                    final ServerData svr = mc.getCurrentServerData();
                    if (!svr.serverIP.equals("")) {
                        if (userNamePrivate) {
                            if (versionPrivate) {
                                details = MODVER + " - Playing as " + mc.player.getName();
                            } else {
                                details = mc.player.getName();
                            }
                        }
                        else {
                            if (versionPrivate) {
                                details = MODVER + " - Playing Multiplayer";
                            } else {
                                details = "Multiplayer";
                            }
                        }
                        if (ipPrivate) {
                            if (hpPrivate) {
                                state = svr.serverIP + " (" + ((int) mc.player.getHealth() / 2) + " hearts)";
                            }
                            else {
                                state = svr.serverIP;
                            }
                            if (svr.populationInfo != null) {
                                popInfo = svr.populationInfo.split("/");
                                if (popInfo.length > 2) {
                                    players2 = Integer.parseInt(popInfo[0]);
                                    maxPlayers2 = Integer.parseInt(popInfo[1]);
                                }
                            }
                        }
                        else {
                            if (hpPrivate) {
                                state = "(" + ((int) mc.player.getHealth() / 2) + " hearts)";
                            }
                            else {
                                state = "";
                            }
                        }
                    }
                }
                else {
                    if (versionPrivate) {
                        details = MODVER + " - Looking at Main Menu";
                    }
                    else {
                        details = "Main Menu";
                    }
                    state = "";
                }
                if (!details.equals(DiscordPresence.presence.details) || !state.equals(DiscordPresence.presence.state)) {
                    DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
                }
                DiscordPresence.presence.details = details;
                DiscordPresence.presence.state = state;
                DiscordPresence.rpc.Discord_UpdatePresence(DiscordPresence.presence);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e3) {
                e3.printStackTrace();
            }
        }
    }

    private static /* synthetic */ void lambdastart0(final int var1, final String var2) {
        System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2);
    }

    static {
        rpc = DiscordRPC.INSTANCE;
        DiscordPresence.presence = new DiscordRichPresence();
        DiscordPresence.hasStarted = false;
    }
}