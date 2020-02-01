package com.gee12.htmlwysiwygeditor;

import android.graphics.Color;

public class ColorUtils {

    /**
     *
     * @param colorString
     * @return
     */
    public static int stringToColor(String colorString) {
        return -1;
    }

    /**
     * Преобразование строки в формате "rgb(r,g,b)" или "rgba(r,g,b)" в Color
     * @param rgbString
     * @return
     */
    public static int rgbStringToColor(String rgbString) {
        try {
            String numsStr = rgbString.substring(rgbString.indexOf('(') + 1, rgbString.indexOf(')'));
            String[] nums = numsStr.split(",");
            int[] chanels = new int[nums.length];
            for (int i = 0; i < nums.length; i++) {
                chanels[i] = Integer.parseInt(nums[i].trim());
            }
            return Color.rgb(chanels[0], chanels[1],chanels[2]);
        } catch (Exception ignored) {
        }
        return -1;
    }

    /**
     * Преобразование строки в формате "#RRGGBB" или "#AARRGGBB" или в виде названия цвета (black,green) в Color
     * @param hexString
     * @return
     */
    public static int hexStringToColor(String hexString) {
        try {
            return Color.parseColor(hexString);
        }
        catch (Exception ignored) {
        }
        return -1;
    }


    public static String colorToHexString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

}
