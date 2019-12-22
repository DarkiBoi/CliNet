package me.zeroeightsix.kami.util;

import com.google.gson.Gson;
import me.zeroeightsix.kami.KamiMod;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;

public class Donators {

    public DonatorUser[] donators;

    public class DonatorUser {
        String uuid;
    }

    public Donators() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(KamiMod.DONATORS_JSON).openConnection();
            connection.connect();
            this.donators = new Gson().fromJson(new InputStreamReader(connection.getInputStream()), DonatorUser[].class);
        } catch (Exception e) {
            KamiMod.log.error("Couldn't load donators.json");
            e.printStackTrace();
        }
    }

}
