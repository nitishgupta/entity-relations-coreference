package edu.illinois.cs.cogcomp.erc.sl.ner.annotators;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.sl.ner.Test;
import edu.illinois.cs.cogcomp.sl.core.SLModel;

/**
 * @author Bhargav Mangipudi
 */
public class BIOAnnotator extends Annotator {
    private SLModel model;
    private boolean is2004;

    public BIOAnnotator(SLModel model, boolean is2004, String predictedBIOViewName) {
        super(predictedBIOViewName, new String[] { ViewNames.TOKENS, ViewNames.SENTENCE });

        this.model = model;
        this.is2004 = is2004;
    }
    @Override
    public void addView(TextAnnotation textAnnotation) throws AnnotatorException {
        for (String requiredViews : this.getRequiredViews()) {
            if (!textAnnotation.hasView(requiredViews)) {
                throw new AnnotatorException("Required View Not found - " + requiredViews);
            }
        }

        try {
            Document doc = new Document(textAnnotation, this.is2004, textAnnotation.getId());
            Test.addNERView(doc, this.viewName, this.model);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
