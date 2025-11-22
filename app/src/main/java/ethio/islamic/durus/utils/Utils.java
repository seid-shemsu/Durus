package ethio.islamic.durus.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

import ethio.islamic.durus.BuildConfig;

public class Utils {

    public static boolean compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");
        int minLength = Math.min(parts1.length, parts2.length);
        for (int i = 0; i < minLength; i++) {
            int part1 = Integer.parseInt(parts1[i]);
            int part2 = Integer.parseInt(parts2[i]);

            if (part1 < part2) {
                return false;
            } else if (part1 > part2) {
                return true;
            }
        }
        return false;
    }

    public static boolean showAd(Context context) {
        boolean show = context.getSharedPreferences("ad", Context.MODE_PRIVATE).getBoolean("show", false);
        boolean isPaid = isPaid(context);
        boolean passed = has24HoursPassed(context);
        return show && !isPaid && passed;
    }

    public static void setPaidUser(Context context, boolean isPaid) {
        context.getSharedPreferences("user", Context.MODE_PRIVATE).edit().putBoolean("is_paid", isPaid).commit();
    }

    private static boolean isPaid(Context context) {
        return context.getSharedPreferences("user", Context.MODE_PRIVATE).getBoolean("is_paid", false);
    }

    public static String getUUID(Context context) {
        String uuid = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("UUID", "");
        if (uuid.isEmpty()) {
            saveUUID(context);
            return getUUID(context);
        }
        return uuid;
    }

    private static void saveUUID(Context context) {
        context.getSharedPreferences("user", Context.MODE_PRIVATE).edit().putString("UUID", UUID.randomUUID().toString()).commit();
    }

    private static final long HOURS_24 = 24 * 60 * 60 * 1000;
    private static final long HOURS_24_DEBUG = 5 * 60 * 1000;

    public static void logUserStartTimeOnce(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        if (!prefs.contains("start_time")) {
            long now = System.currentTimeMillis();
            prefs.edit().putLong("start_time", now).apply();
        }
    }

    private static boolean has24HoursPassed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        long savedTime = prefs.getLong("start_time", 0);
        if (savedTime == 0) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (BuildConfig.DEBUG) {
            return (now - savedTime) >= HOURS_24_DEBUG;
        } else {
            return (now - savedTime) >= HOURS_24;
        }
    }

}
