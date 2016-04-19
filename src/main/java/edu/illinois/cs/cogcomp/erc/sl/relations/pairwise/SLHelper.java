package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise;

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

    public static List<Pair<RelationMentionPair, RelationLabel>> populateSLProblemForDocument(
            Document document,
            Lexiconer lm,
            String entityViewName,
            String relationViewName) {
        TextAnnotation ta = document.getTA();
        assert ta.hasView(entityViewName);
        assert ta.hasView(relationViewName);

        PredicateArgumentView relationSourceView = (PredicateArgumentView) ta.getView(relationViewName);

        List<Pair<RelationMentionPair, RelationLabel>> problemInstances = new ArrayList<>();
        List<Constituent> argumentListForNegativeSampling = new ArrayList<>();

        for (Constituent predicate : relationSourceView.getPredicates()) {
            Constituent relArg = predicate.getOutgoingRelations().get(0).getTarget();
            String label = predicate.getAttribute(ACEReader.RelationTypeAttribute);

            RelationMentionPair instance = new RelationMentionPair(predicate, relArg);
            RelationLabel structure = new RelationLabel(label);

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

                RelationMentionPair instance = new RelationMentionPair(predicate, randCons);
                RelationLabel structure = new RelationLabel(NO_RELATION_LABEL);

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
