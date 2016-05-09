package edu.illinois.cs.cogcomp.erc.util;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

/**
 * @author Bhargav Mangipudi
 */
public class ChainedAnnotator extends Annotator {
    Annotator[] annotators;

    public ChainedAnnotator(Annotator... annotators) {
        super(annotators[annotators.length - 1].getViewName(), annotators[0].getRequiredViews());
        this.annotators = annotators;
    }

    @Override
    public void addView(TextAnnotation textAnnotation) throws AnnotatorException {
        for (Annotator ann : annotators) {
            ann.addView(textAnnotation);
        }
    }
}
