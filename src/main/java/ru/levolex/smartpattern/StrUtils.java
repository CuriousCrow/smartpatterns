package ru.levolex.smartpattern;

public class StrUtils {

    public static String removeEndings(String str) {
        if (str.length() < 3)
            return str;
        return str.substring(1, str.length() - 1);
    }

}
