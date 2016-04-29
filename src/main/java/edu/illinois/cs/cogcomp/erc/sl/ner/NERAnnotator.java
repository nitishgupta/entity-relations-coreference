package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.util.Utils;
import edu.illinois.cs.cogcomp.sl.core.SLModel;

/**
 * @author Bhargav Mangipudi
 */
public class NERAnnotator extends Annotator {
    private SLModel model;

    public NERAnnotator(SLModel model) {
        // Change this to "NER_ACE_COARSE"
        super("ENTITYVIEW", new String[] { ViewNames.TOKENS, ViewNames.SENTENCE });

        this.model = model;
    }

    public void setSLModel(SLModel model) {
        this.model = model;
    }

    @Override
    public void addView(TextAnnotation textAnnotation) throws AnnotatorException {
        for (String requiredViews : this.getRequiredViews()) {
            if (!textAnnotation.hasView(requiredViews)) {
                throw new AnnotatorException("Required View Not found - " + requiredViews);
            }
        }

        try {
            // Add the POS View
            Document doc = new Document(textAnnotation, false, textAnnotation.getId());

            Test.addNERView(doc, this.viewName + "_BIO", this.model);

            TokenLabelView bioView = (TokenLabelView) textAnnotation.getView(this.viewName + "_BIO");
            SpanLabelView nerView = new SpanLabelView(this.viewName, NERAnnotator.class.getCanonicalName(), textAnnotation, 1.0);

            int currentChunkStart = 0;
            int currentChunkEnd = 0;

            String clabel = "";
            Constituent previous = null;
            for (Constituent bioToken : bioView.getConstituents()) {
                String currentLabel = bioToken.getLabel();

                // what happens if we see an Inside tag -- even if it doesn't follow a Before tag
                if (null != currentLabel && currentLabel.charAt(0) == 'I') {
                    if (null == clabel) // we must have just seen an Outside tag and possibly completed
                    // a chunk
                    {
                        // modify lbjToken.type for later ifs
                        currentLabel = "B" + currentLabel.substring(1);
                    } else if (!clabel.equals(currentLabel.substring(2))) {
                        // trying to avoid mysterious null pointer exception...
                        currentLabel = "B" + currentLabel.substring(1);
                    }
                }
                if ((currentLabel.charAt(0) == 'B' || currentLabel.charAt(0) == 'O')
                        && clabel != null) {

                    if (previous != null) {
                        currentChunkEnd = previous.getEndSpan();
                        Constituent label = new Constituent(
                                clabel,
                                this.viewName,
                                textAnnotation,
                                currentChunkStart,
                                currentChunkEnd);

                        nerView.addConstituent(label);
                        clabel = null;
                    } // else no chunk in progress (we are at the start of the doc)
                }

                if (currentLabel.charAt(0) == 'B') {
                    currentChunkStart = bioToken.getStartSpan();
                    clabel = currentLabel.substring(2);
                }
                previous = bioToken;
            }
            if (clabel != null && null != previous) {
                currentChunkEnd = previous.getEndSpan();
                Constituent label = new Constituent(
                        clabel,
                        this.viewName,
                        textAnnotation,
                        currentChunkStart,
                        currentChunkEnd);

                nerView.addConstituent(label);
            }

            textAnnotation.addView(this.viewName, nerView);

            Utils.printTAConstitutents(nerView.getConstituents());
        } catch(Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
}
