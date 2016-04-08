package edu.illinois.cs.cogcomp.erc.sl.ner;

/**
 * Created by Bhargav Mangipudi on 3/12/16.
 */

// TODO:This needs to be serialized with the model as  changing this will change the functioning of the lexiconer.
public class LexiconerConstants {
    public static final String WORD_PREFIX = "word:";
    public static final String WORD_UNKNOWN = "word:unknownword";
    public static final String LABEL_PREFIX = "label:";

    public static final String POS_PREFIX = "pos:";
    public static final String POS_UNKNOWN = "pos:unknown";
}
