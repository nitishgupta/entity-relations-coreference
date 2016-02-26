package edu.illinois.cs.cogcomp.corpus;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;

import java.util.Map;

/**
 * Created by nitishgupta on 2/21/16.
 */
public class Corpus {
    Map<ACEDocument, TextAnnotation> docs;
    boolean isACE04;
}
