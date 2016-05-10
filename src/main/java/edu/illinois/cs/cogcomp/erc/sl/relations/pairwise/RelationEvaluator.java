package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise;

import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.experiments.ClassificationTester;
import edu.illinois.cs.cogcomp.core.experiments.evaluators.Evaluator;

import java.util.*;

/**
 * @author Bhargav Mangipudi
 */
public class RelationEvaluator extends Evaluator {
    public PredicateArgumentView goldRelationView;
    public PredicateArgumentView predictedRelationView;

    @Override
    public void evaluate(ClassificationTester classificationTester, View goldView, View predictedView) {
        this.goldRelationView = (PredicateArgumentView) goldView;
        this.predictedRelationView = (PredicateArgumentView) predictedView;

        // Assumes that relation view has same span as entities.
        Set<Constituent> predicatesInGold = new HashSet<>(this.goldRelationView.getPredicates());
        Set<Constituent> predicatesInPredicted = new HashSet<>(this.predictedRelationView.getPredicates());

        assert predicatesInGold.size() == this.goldRelationView.getPredicates().size();
        assert predicatesInPredicted.size() == this.predictedRelationView.getPredicates().size();

        for (Constituent goldPredicate : predicatesInGold) {
            Relation goldRelation = goldPredicate.getOutgoingRelations().get(0);
            Constituent goldArgument = goldRelation.getTarget();

            Relation matchingRelation = null;
            for (Constituent predicted : predicatesInPredicted) {
                if (predicted.getSpan().equals(goldPredicate.getSpan())) {
                    Relation pred = predicted.getOutgoingRelations().get(0);

                    if (goldArgument.getSpan().equals(pred.getTarget().getSpan())) {
                        matchingRelation = pred;
                    }
                }
            }

            if (matchingRelation == null) {
                classificationTester.recordGoldOnly(goldRelation.getRelationName());
            } else {
                predicatesInPredicted.remove(matchingRelation.getSource());
                classificationTester.record(goldRelation.getRelationName(), matchingRelation.getRelationName());
            }
        }

        for (Constituent remainPredicted : predicatesInPredicted) {
            Relation pred = remainPredicted.getOutgoingRelations().get(0);
            classificationTester.recordPredictionOnly(pred.getRelationName());
        }
    }
}
