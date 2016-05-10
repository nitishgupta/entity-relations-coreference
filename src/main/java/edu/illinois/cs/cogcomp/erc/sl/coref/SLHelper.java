package edu.illinois.cs.cogcomp.erc.sl.coref;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitishgupta on 5/10/16.
 */
public class SLHelper {

    public static List<Pair<CorefMentionPair, CorefLabel>> populateSLProblemForDocument(Document document,
                                                                                        Lexiconer lm,
                                                                                        String EntityViewName,
                                                                                        String CorefViewName){
        TextAnnotation ta = document.getTA();
        assert ta.hasView(EntityViewName) : "Entity View Not present";
        assert ta.hasView(CorefViewName) : "Coref View Not Present";
        View corefView = ta.getView(CorefViewName);
        List<Pair<CorefMentionPair, CorefLabel>> problemInstances = new ArrayList<>();


        /*
        POPULATE PROBLEM INSTANCES
        */

        return problemInstances;

    }
}
