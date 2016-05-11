package edu.illinois.cs.cogcomp.erc.sl.coref;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import scala.collection.immutable.Stream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nitishgupta on 5/10/16.
 */
public class SLHelper {

    public static List<Pair<CorefMentionPair, CorefLabel>> populateSLProblemForDocument(Document document,
                                                                                        Lexiconer lm,
                                                                                        String EntityViewName,
                                                                                        String CorefViewName){
        List<Pair<CorefMentionPair, CorefLabel>> problemInstances = new ArrayList<>();
        TextAnnotation ta = document.getTA();
        assert ta.hasView(EntityViewName) : "Entity View Not present";
        assert ta.hasView(CorefViewName) : "Coref View Not Present";
        CoreferenceView corefView = (CoreferenceView) ta.getView(CorefViewName);
        List<Constituent> constituents = corefView.getConstituents();   // Sorting constituents in increasing order
        constituents = sortIncreasing(constituents);

        // Consituents are in left-to-right order now
        // At any constituent, start looking left, if same cluster then positive, if other then negative.
        // Cap on negative links?

        int numConstituents = constituents.size();
        for(int i=0; i<numConstituents; i++){
            Constituent currC = constituents.get(i);
            String currID = currC.getLabel();
            int past = i-1, negadded = 0;
            while(past >=0){
                Constituent prevC = constituents.get(past);
                String prevID = prevC.getLabel();
                if(currID.equals(prevID)){
                    CorefMentionPair CExample = new CorefMentionPair(currC, prevC);
                    CorefLabel CValue = new CorefLabel(CorefLabel.t);
                    problemInstances.add(new Pair<>(CExample, CValue));
                } else{
                    if(negadded < 5) {
                        CorefMentionPair CExample = new CorefMentionPair(currC, prevC);
                        CorefLabel CValue = new CorefLabel(CorefLabel.f);
                        problemInstances.add(new Pair<>(CExample, CValue));
                        negadded++;
                    }
                }
                past--;
            }
        }

        if(lm.isAllowNewFeatures()){
            lm.addLabel(CorefLabel.f);
            lm.addLabel(CorefLabel.t);
        }
        return problemInstances;
    }

    public static List<Constituent> sortIncreasing(List<Constituent> constituents){
        //noinspection Since15
        constituents.sort(new Comparator<Constituent>(){
            @Override
            public int compare(Constituent o1, Constituent o2) {
                if(o1.getStartSpan() >= o2.getStartSpan() )
                    return 1;
                else
                    return -1;
            }
        } );
        return constituents;
    }


    public static Pair<CorefMentionPair, CorefLabel> getTestCExamplePair(Constituent c1, Constituent c2, String label){
        CorefMentionPair CExample = new CorefMentionPair(c1, c2);
        CorefLabel CLabel = new CorefLabel(label);
        return new Pair<CorefMentionPair, CorefLabel>(CExample, CLabel);
    }
}
