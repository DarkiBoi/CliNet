package me.zeroeightsix.kami.util;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.ChunkCache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by TBM on 13/12/2019.
 */
public class Enemies {
    public static final Enemies INSTANCE = new Enemies();

    public static Setting<ArrayList<Enemy>> enemies;

    private Enemies() {
    }

    public static void initEnemy() {
        enemies = Settings.custom("Enemy", new ArrayList<Enemy>(), new EnemyListConverter()).buildAndRegister("enemies");
    }

    public static boolean isEnemy(String name) {
        return enemies.getValue().stream().anyMatch(enemy -> enemy.username.equalsIgnoreCase(name));
    }

    /*Checks if Enemy is in HitRange (Parameter range)*/
    public static boolean isEnemyNearby(double range) {
        for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
            if (EntityUtil.isLiving(e)) {
                if (Enemies.isEnemy(e.getName())) {
                    if (Minecraft.getMinecraft().player.getDistance(e) > range) {
                        return false;
                    } else if (Minecraft.getMinecraft().player.getDistance(e) <= range) {
                        return true;
                    }
                }
            }
        } return false;
    }

    public static class Enemy {
        String username;
        UUID uuid;

        public Enemy(String username, UUID uuid) {
            this.username = username;
            this.uuid = uuid;
        }

        public String getUsername() {
            return username;
        }
    }

    public static class EnemyListConverter extends Converter<ArrayList<Enemy>, JsonElement> {
        public EnemyListConverter() {}

        @Override
        protected JsonElement doForward(ArrayList<Enemy> list) {
            StringBuilder present = new StringBuilder();
            for (Enemy enemy : list)
                present.append(String.format("%s;%s$", enemy.username, enemy.uuid.toString()));
            return new JsonPrimitive(present.toString());
        }

        @Override
        protected ArrayList<Enemy> doBackward(JsonElement jsonElement) {
            String v = jsonElement.getAsString();
            String[] pairs = v.split(Pattern.quote("$"));
            ArrayList<Enemy> enemies = new ArrayList<>();
            for (String pair : pairs) {
                try {
                    String[] split = pair.split(";");
                    String username = split[0];
                    UUID uuid = UUID.fromString(split[1]);
                    enemies.add(new Enemy(getUsernameByUUID(uuid, username),uuid));
                } catch (Exception ignored) {} // Empty line, wrong formatting or something, we don't care
            }
            return enemies;
        }

        private String getUsernameByUUID(UUID uuid, String saved) {
            String src = getSource("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
            if (src == null || src.isEmpty()) return saved;
            try {
                JsonElement object = new JsonParser().parse(src);
                return object.getAsJsonObject().get("name").getAsString();
            }catch (Exception e) {
                e.printStackTrace();
                System.err.println(src);
                return saved;
            }
        }

        private static String getSource(String link) {
            try{
                URL u = new URL(link);
                URLConnection con = u.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    buffer.append(inputLine);
                in.close();

                return buffer.toString();
            }catch(Exception e) {
                return null;
            }
        }
    }

}
