package utilities;

public class StringTrimUtility {

    public static String trimString(String str) {
        str = str.replace("[", "").trim();
        str = str.replace("]", "").trim();
        return str;
    }
}
