package me.zeroeightsix.kami.util;

import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.module.ModuleManager;
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
    private static HashMap<String, String> UUIDandCapes = new HashMap<>();

    public enum capeUserType {
        DONATOR, UPPERDONATOR, DEV
    }


    public CapeManager() {
        capeUsers = new HashMap<>();
    }

    public static boolean hasCape(final UUID uuid) {
        if(ModuleManager.getModuleByName("Capes").isDisabled()) {
            return false;
        }
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

    public static String getImgName(final UUID uuid) {
        if (hasCape(uuid) && isUpperDonator(uuid)) {
            return UUIDandCapes.get(sanitizeUuid(uuid));
        }
        return "donatorcape.png";
    }

    public static String sanitizeUuid(UUID uuid) {
        return sanitizeUuidString(uuid.toString());
    }

    private static String sanitizeUuidString(String uuidString) {
        return uuidString.replaceAll("-", "").toLowerCase();
    }

    public void initializeCapes() {

        getFromGithubCapes(donateHigherGithub).forEach((uuid, cape) -> {
            UUIDandCapes.put(uuid, cape);
        });

        getFromGithubWithoutCapes(donateHigherGithub).forEach(uuid -> {
            capeUsers.put(uuid, capeUserType.UPPERDONATOR);
        });

        getFromGithub(donateGithub).forEach(uuid -> {
            capeUsers.put(uuid, capeUserType.DONATOR);
        });

        getFromGithub(devGithub).forEach(uuid -> {
            capeUsers.put(uuid, capeUserType.DEV);
        });



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

    private HashMap<String, String> getFromGithubCapes(String urlString) {

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

        HashMap<String, String> uuidAndCapeList = new HashMap<String, String>();
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
            uuidAndCapeList.put(sanitizeUuidString(split[0]), split[1]);
        }

        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

        return uuidAndCapeList;

    }

    private List<String> getFromGithubWithoutCapes(String urlString) {

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

        List<String> uuidList = new ArrayList<>();
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
                return new ArrayList<>();
            }
            uuidList.add(sanitizeUuidString(split[0]));
        }

        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return uuidList;

    }




}
