package edu.illinois.cs.cogcomp.erc.ir;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nitishgupta on 3/7/16.
 */
public class DocUtils {



    public static void addNERBIOView(Document doc){
        TextAnnotation ta = doc.getTA();

        View ner_coarse = ta.getView(Corpus.NER_GOLD_COARSE_VIEW);
        View tokens = ta.getView(Corpus.TOKENS_VIEW);
        View ner_bio = new View(Corpus.NER_GOLD_BIO_VIEW, DocUtils.class.getCanonicalName(), ta, 1.0D);

        Map<Integer, Integer> start_end = new HashMap<Integer, Integer>();
        for(Constituent c : ner_coarse.getConstituents()){
            start_end.put(c.getStartSpan(), c.getEndSpan()-1);
        }

        List<Constituent> tokens_constituents = tokens.getConstituents();

        int i = 0;
        while(i < tokens_constituents.size()){
            Constituent token = tokens_constituents.get(i);
            Constituent c_bio = null;
            // Either token i is start of NER type or OUTSIDE
            if(start_end.containsKey(i)){
                int end = start_end.get(i);
                List<Constituent> cons = ner_coarse.getConstituentsCoveringToken(i);
                String label = cons.get(0).getLabel();
                c_bio = token.cloneForNewViewWithDestinationLabel(Corpus.NER_GOLD_BIO_VIEW, "B-"+label);
                ner_bio.addConstituent(c_bio);
                i++;
                while(i<=end){
                    token = tokens_constituents.get(i);
                    c_bio = token.cloneForNewViewWithDestinationLabel(Corpus.NER_GOLD_BIO_VIEW, "I-" + label);
                    ner_bio.addConstituent(c_bio);
                    i++;
                }

            }

            else{
                c_bio = token.cloneForNewViewWithDestinationLabel(Corpus.NER_GOLD_BIO_VIEW, "O");
                ner_bio.addConstituent(c_bio);
                i++;
            }
        }

        ta.addView(Corpus.NER_GOLD_BIO_VIEW, ner_bio);
    }
}
