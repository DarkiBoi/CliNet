package me.zeroeightsix.kami.command.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.command.syntax.ChunkBuilder;
import me.zeroeightsix.kami.command.syntax.parsers.EnumParser;
import me.zeroeightsix.kami.util.Enemies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by TBM on 13/12/2019.
 */
public class EnemyCommand extends Command {

    public EnemyCommand() {
        super("enemy", new ChunkBuilder()
                .append("mode", true, new EnumParser(new String[]{"add", "del"}))
                .append("name")
                .build());
    }

    @Override
    public void call(String[] args) {
        if (args[0] == null) {
            if (Enemies.INSTANCE.enemies.getValue().isEmpty()) {
                Command.sendChatMessage("You currently don't have any enemies added. &benemy add <name>&r to add one.");
                return;
            }
            String e = "";
            for (Enemies.Enemy enemy : Enemies.INSTANCE.enemies.getValue())
                e += enemy.getUsername() + ", ";
            e = e.substring(0,e.length()-2);
            Command.sendChatMessage("Your enemies: " + e);
            return;
        }else {
            if (args[1] == null) {
                Command.sendChatMessage(String.format(Enemies.isEnemy(args[0]) ? "Yes, %s is your enemy." : "No, %s isn't a enemy of yours.", args[0]));
                Command.sendChatMessage(String.format(Enemies.isEnemy(args[0]) ? "Yes, %s is your enemy." : "No, %s isn't a enemy of yours.", args[0]));
                return;
            }

            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("new")) {
                if (Enemies.isEnemy(args[1])) {
                    Command.sendChatMessage("That player is already your enemy.");
                    return;
                }

                // New thread because of potential internet connection made
                new Thread(() -> {
                    Enemies.Enemy e = getEnemyByName(args[1]);
                    if (e == null) {
                        Command.sendChatMessage("Failed to find UUID of " + args[1]);
                        return;
                    }
                    Enemies.INSTANCE.enemies.getValue().add(e);
                    Command.sendChatMessage("&b" + e.getUsername() + "&r has been enemied.");
                }).start();

                return;
            }else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")) {
                if (!Enemies.isEnemy(args[1])) {
                    Command.sendChatMessage("That player isn't your enemy.");
                    return;
                }

                Enemies.Enemy enemy = Enemies.INSTANCE.enemies.getValue().stream().filter(enemy1 -> enemy1.getUsername().equalsIgnoreCase(args[1])).findFirst().get();
                Enemies.INSTANCE.enemies.getValue().remove(enemy);
                Command.sendChatMessage("&b" + enemy.getUsername() + "&r has been unenemied.");
                return;
            }else {
                Command.sendChatMessage("Please specify either &6add&r or &6remove");
                return;
            }
        }
    }

    private Enemies.Enemy getEnemyByName(String input) {
        ArrayList<NetworkPlayerInfo> infoMap = new ArrayList<NetworkPlayerInfo>(Minecraft.getMinecraft().getConnection().getPlayerInfoMap());
        NetworkPlayerInfo profile = infoMap.stream().filter(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(input)).findFirst().orElse(null);
        if (profile == null) {
            Command.sendChatMessage("Player isn't online. Looking up UUID..");
            String s = requestIDs("[\"" + input + "\"]");
            if (s == null || s.isEmpty()) {
                Command.sendChatMessage("Couldn't find player ID. Are you connected to the internet? (0)");
            }else {
                JsonElement element = new JsonParser().parse(s);
                if (element.getAsJsonArray().size()==0) {
                    Command.sendChatMessage("Couldn't find player ID. (1)");
                }else {
                    try {
                        String id = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                        String username = element.getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
                        Enemies.Enemy enemy = new Enemies.Enemy(username, UUIDTypeAdapter.fromString(id));
                        return enemy;
                    }catch (Exception e) {
                        e.printStackTrace();
                        Command.sendChatMessage("Couldn't find player ID. (2)");
                    }
                }
            }
            return null;
        }
        Enemies.Enemy e = new Enemies.Enemy(profile.getGameProfile().getName(), profile.getGameProfile().getId());
        return e;
    }

    private static String requestIDs(String data) {
        try{
            String query = "https://api.mojang.com/profiles/minecraft";
            String json = data;

            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String res = convertStreamToString(in);
            in.close();
            conn.disconnect();

            return res;
        }catch (Exception e) {
            return null;
        }
    }

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String r = s.hasNext() ? s.next() : "/";
        return r;
    }
}
