package edu.illinois.cs.cogcomp.erc.util;

/**
 * Created by bhargav on 2/26/16.
 */
public class Utils {

    public static String getCorpusTypeFromFilename(String filename) {
        return filename.substring(0, filename.indexOf('/'));
    }
}
