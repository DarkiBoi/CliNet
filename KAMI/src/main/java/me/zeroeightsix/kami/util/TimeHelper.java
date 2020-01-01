package me.zeroeightsix.kami.util;

public class TimeHelper {

    public static long lastMS = 0L;

    public boolean isDelayComplete(float f) {
        if(System.currentTimeMillis() - lastMS >= f) {
            return true;
        } return false;
    }

    public static long getCurrentMS() {
        return System.currentTimeMillis() / 10000000L;
    }

    public void setLastMS() {
        this.lastMS = System.currentTimeMillis();
    }

    public int convertToMS(int perSecond) {
        return 1000 / perSecond;
    }

    public static boolean hasReached(long ms) {
        return getCurrentMS() - lastMS >= ms;
    }

    public static void reset() {
        lastMS = getCurrentMS();
    }

}
