package edu.illinois.cs.cogcomp.erc.sl.relations;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Bhargav Mangipudi
 */
public class SLHelper {

    public static final String NO_RELATION_LABEL = "NIL";

    public static List<Pair<SLInstance, SLStructure>> populateSLProblemForDocument(
            Document document,
            Lexiconer lm,
            String entityViewName,
            String relationViewName) {
        TextAnnotation ta = document.getTA();
        assert ta.hasView(entityViewName);
        assert ta.hasView(relationViewName);

        PredicateArgumentView relationSourceView = (PredicateArgumentView) ta.getView(relationViewName);

        List<Pair<SLInstance, SLStructure>> problemInstances = new ArrayList<>();
        List<Constituent> argumentListForNegativeSampling = new ArrayList<>();

        for (Constituent predicate : relationSourceView.getPredicates()) {
            Constituent relArg = predicate.getOutgoingRelations().get(0).getTarget();
            String label = predicate.getAttribute(ACEReader.RelationTypeAttribute);

            SLInstance instance = new SLInstance(predicate, relArg);
            SLStructure structure = new SLStructure(label);

            if (lm.isAllowNewFeatures()) {
                lm.addLabel(label);
            }

            problemInstances.add(new Pair<>(instance, structure));
            argumentListForNegativeSampling.add(predicate);
            argumentListForNegativeSampling.add(relArg);
        }

        // TODO: Revisit sampling strategy
        int samplingCount = 1;
        int numArguments = argumentListForNegativeSampling.size();
        Random rand = new Random();

        for (Constituent predicate : relationSourceView.getPredicates()) {
            int added = 0;
            int attempts = 3;

            while (added < samplingCount && attempts > 0) {
                attempts--;

                Constituent randCons = argumentListForNegativeSampling.get(rand.nextInt(numArguments));

                if (randCons.equals(predicate) ||
                        (randCons.getIncomingRelations().size() > 0 && randCons.getIncomingRelations().get(0).getSource() == predicate)) {
                    continue;
                }

                SLInstance instance = new SLInstance(predicate, randCons);
                SLStructure structure = new SLStructure(NO_RELATION_LABEL);

                problemInstances.add(new Pair<>(instance, structure));

                added++;
            }
        }

        if (lm.isAllowNewFeatures()) {
            lm.addLabel(NO_RELATION_LABEL);
        }

        return problemInstances;
    }
}
