package com.dragonpass.intlapp;


/**
 * @author DarkAdventurer
 */
public class PluginUtils {

    public static boolean isEmpty(CharSequence s) {
        if (s == null) {
            return true;
        } else {
            return s.length() == 0;
        }
    }


    public static String getPgyIdentifier() {
//        return "xcxwo";
        return "pgyer";
    }
}
