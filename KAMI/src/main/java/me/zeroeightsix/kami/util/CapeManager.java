package me.zeroeightsix.kami.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class CapeManager {

    private static final String donateGithub = "https://raw.githubusercontent.com/CliNetMC/capes/master/donators";
    private static final String donateHigherGithub = "https://raw.githubusercontent.com/CliNetMC/capes/master/donatorsHigher";
    private static final String devGithub = "https://raw.githubusercontent.com/CliNetMC/capes/master/devs";

    private static HashMap<String, capeUserType> capeUsers;
    private static HashMap<String, String> higherCapeUsers;

    public enum capeUserType {
        DONATOR, UPPERDONATOR, DEV
    }

    public CapeManager() {
        capeUsers = new HashMap<>();
    }

    public static boolean hasCape(final UUID uuid) {
        return capeUsers.containsKey(sanitizeUuid(uuid));
    }

    public static boolean isUpperDonator(final UUID uuid) {
        if (hasCape(uuid)) {
            if (capeUsers.get(sanitizeUuid(uuid)) == capeUserType.UPPERDONATOR) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDev(final UUID uuid) {
        if (hasCape(uuid)) {
            if (capeUsers.get(sanitizeUuid(uuid)) == capeUserType.DEV) {
                return true;
            }
        }
        return false;
    }

    public static String upperGetLocation(final UUID uuid) {
        if (hasCape(uuid) && isUpperDonator(uuid)) {
            return higherCapeUsers.get(sanitizeUuid(uuid));
        }
        return "textures/donatorcape.png";
    }


    private static String sanitizeUuid(UUID uuid) {
        return sanitizeUuidString(uuid.toString());
    }

    private static String sanitizeUuidString(String uuidString) {
        return uuidString.replaceAll("-", "").toLowerCase();
    }

    public void initializeCapes() {

        getFromGithub(donateGithub).forEach(uuid -> {
            capeUsers.put(uuid, capeUserType.DONATOR);
        });

        BiConsumer<String, String> action = new UpperDonatorBiConsumer();

        getFromGithubUpper(donateHigherGithub).forEach(action);

        getFromGithub(devGithub).forEach(uuid -> {
            capeUsers.put(uuid, capeUserType.DEV);
        });

    }

    private HashMap<String, String> getFromGithubUpper(String urlString) {

        URL url;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

        HashMap<String, String> uuidAndImage = new HashMap<String, String>();
        String line;
        String[] split;

        while (true) {
            try {
                if ((line = bufferedReader.readLine()) == null) {
                    break;
                }
                split = line.split(":");
            } catch (IOException e) {
                e.printStackTrace();
                return new HashMap<>();
            }
            uuidAndImage.put(sanitizeUuidString(split[0]), split[1]);
        }

        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

        return uuidAndImage;

    }

    private List<String> getFromGithub(String urlString) {

        URL url;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        ArrayList<String> uuidList = new ArrayList<>();
        String line;

        while (true) {
            try {
                if ((line = bufferedReader.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
            uuidList.add(sanitizeUuidString(line));
        }

        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return uuidList;

    }

    class UpperDonatorBiConsumer implements BiConsumer<String, String> {

        @Override
        public void accept(String k, String v) {
            capeUsers.put(k, capeUserType.UPPERDONATOR);
            try {
                DynamicTexture texture = new DynamicTexture(ImageIO.read(new URL("https://raw.githubusercontent.com/CliNetMC/capes/master/" + v)));
                ResourceLocation location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("textures/capes", texture);
                higherCapeUsers.put(k, location.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
