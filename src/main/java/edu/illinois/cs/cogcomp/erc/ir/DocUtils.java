package edu.illinois.cs.cogcomp.erc.ir;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nitishgupta on 3/7/16.
 */
public class DocUtils {

    public static void addNERCoarseBIOView(Document doc) {
        DocUtils.addBIOView(doc, Corpus.NER_GOLD_COARSE_VIEW, Corpus.NER_GOLD_BIO_VIEW);
    }

    /**
     * Adds a BIO (Begin, Inside, Outside) representation view for an input SpanLabel View.
     * @param doc Document to be annotated.
     * @param inputViewName Name of the source view. (eg. NER_ACE_COARSE)
     * @param outputViewName Name for the output view. (eg. NER_GOLD_COARSE_BIO)
     */
    public static void addBIOView(Document doc, String inputViewName, String outputViewName){
        TextAnnotation ta = doc.getTA();

        View inputView = ta.getView(inputViewName);

        View tokens = ta.getView(ViewNames.TOKENS);
        View bioView = new TokenLabelView(outputViewName, DocUtils.class.getCanonicalName(), ta, 1.0D);

        Map<Integer, Integer> start_end = new HashMap<Integer, Integer>();
        for (Constituent c : inputView.getConstituents()) {
            start_end.put(c.getStartSpan(), c.getEndSpan() - 1);
        }

        List<Constituent> tokens_constituents = tokens.getConstituents();

        int i = 0;
        while (i < tokens_constituents.size()) {
            Constituent token = tokens_constituents.get(i);
            Constituent c_bio;

            // Either token i is start of NER type or OUTSIDE
            if(start_end.containsKey(i)) {
                int end = start_end.get(i);
                List<Constituent> cons = inputView.getConstituentsCoveringToken(i);
                String label = cons.get(0).getLabel();

                c_bio = token.cloneForNewViewWithDestinationLabel(outputViewName, "B-"+label);
                bioView.addConstituent(c_bio);
                i++;

                while (i <= end) {
                    token = tokens_constituents.get(i);
                    c_bio = token.cloneForNewViewWithDestinationLabel(outputViewName, "I-" + label);
                    bioView.addConstituent(c_bio);
                    i++;
                }
            }
            else {
                c_bio = token.cloneForNewViewWithDestinationLabel(outputViewName, "O");
                bioView.addConstituent(c_bio);
                i++;
            }
        }

        ta.addView(outputViewName, bioView);
    }
}
