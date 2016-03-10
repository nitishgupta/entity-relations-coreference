package edu.illinois.cs.cogcomp.erc.config;

/**
 * Created by nitishgupta on 2/25/16.
 */
public class Parameters {

    public static String configFile = "config/Parameters.config";

    public static String ACE04_DATA_DIR;
    public static String ACE05_DATA_DIR;

    public static String ACE04_FILELIST;
    public static String ACE05_FILELIST;

    public static String ACE05_TRAIN_FILELIST;
    public static String ACE05_DEV_FILELIST;
    public static String ACE05_TEST_FILELIST;

    public static String ACE04_SERIALIZED_DOCS;
    public static String ACE05_SERIALIZED_DOCS;

    public static String SL_PARAMETER_CONFIG_FILE;

    public static boolean isDebug = false;

    public static String ACE05_SERIALIZED_CORPUS = "index/ace05/corpus.ser";
    public static String ACE04_SERIALIZED_CORPUS = "index/ace04/corpus.ser";

    public static double train_perc = 0.75;
    public static double dev_perc = 0.15;
    public static double test_perc = 0.1;
}
