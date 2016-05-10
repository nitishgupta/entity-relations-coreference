package edu.illinois.cs.cogcomp.erc.config;

/**
 * Created by nitishgupta on 2/25/16.
 */
public class Parameters {

    public static final String configFile = "config/Parameters.config";

    public static String ACE04_DATA_DIR;
    public static String ACE05_DATA_DIR;

    public static String ACE04_SECTION_LIST;
    public static String ACE05_SECTION_LIST;

    public static String ACE04_SERIALIZED_DOCS;
    public static String ACE05_SERIALIZED_DOCS;

    public static String SL_PARAMETER_CONFIG_FILE;

    public static String ACE05_SERIALIZED_CORPUS;
    public static String ACE04_SERIALIZED_CORPUS;

    public static String RELATION_PAIRWISE_MENTION_VIEW_GOLD;
    public static String RELATION_PAIRWISE_RELATION_VIEW_GOLD;
    public static String RELATION_PAIRWISE_RELATION_VIEW_PREDICTION;

    public static String COREF_VIEW;

    public static double train_perc = 0.75;
    public static double dev_perc = 0.15;
    public static double test_perc = 0.2;
}
