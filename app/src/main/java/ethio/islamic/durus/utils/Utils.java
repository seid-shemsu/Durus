package ethio.islamic.durus.utils;

import android.content.Context;

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
        return context.getSharedPreferences("ad", Context.MODE_PRIVATE).getBoolean("show", false);
    }
}
