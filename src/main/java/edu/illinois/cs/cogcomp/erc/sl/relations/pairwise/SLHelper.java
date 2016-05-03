package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise;

import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.QueryableList;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.ArrayList;
import java.util.Collections;
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

        for (Constituent predicate : relationSourceView.getPredicates()) {
            Relation relation = predicate.getOutgoingRelations().get(0);
            Constituent relArg = relation.getTarget();
            String label = relation.getRelationName();

            RelationMentionPair instance = new RelationMentionPair(predicate, relArg);
            RelationLabel structure = new RelationLabel(label);

            if (lm.isAllowNewFeatures()) {
                lm.addLabel(label);
            }

            problemInstances.add(new Pair<>(instance, structure));
        }

        if (lm.isAllowNewFeatures()) {
            lm.addLabel(NO_RELATION_LABEL);
        }

        for (Pair<RelationMentionPair, RelationLabel> items : sampleNegativeRelations(
                document,
                lm,
                entityViewName,
                relationViewName,
                0.2)) {
            problemInstances.add(items);
        }

        return problemInstances;
    }

    public static List<Pair<RelationMentionPair, RelationLabel>> sampleNegativeRelations(
            Document document,
            Lexiconer lm,
            String entityViewName,
            String relationViewName,
            double fractionOfRelations) {

        Random rand = new Random();

        PredicateArgumentView relationView = (PredicateArgumentView) document.getTA().getView(relationViewName);

        int numOfRelation = relationView.getPredicates().size();
        int toSample = (int) (numOfRelation * fractionOfRelations);

        List<Pair<RelationMentionPair, RelationLabel>> sampledRelations = new ArrayList<>(toSample);
        SpanLabelView entityView = (SpanLabelView) document.getTA().getView(entityViewName);

        int addedEntities = 0;
        int attempts = 2;
        int numEntities = entityView.getNumberOfConstituents();

        IQueryable<Constituent> predicates = new QueryableList<>(relationView.getPredicates());

        while (addedEntities < toSample && attempts > 0) {
            int firstEntity = rand.nextInt(numEntities);
            int secondEntity = rand.nextInt(numEntities);

            attempts--;
            if (firstEntity == secondEntity) {
                continue;
            }

            Constituent firstItem = entityView.getConstituents().get(firstEntity);
            Constituent secondItem = entityView.getConstituents().get(secondEntity);

            boolean hasRelation = false;
            IQueryable<Constituent> matchPredicateResult = predicates.where(Queries.sameSpanAsConstituent(firstItem));
            if (matchPredicateResult.count() > 0) {
                for (Constituent pred : matchPredicateResult) {
                    if (pred.getStartSpan() == secondItem.getStartSpan() && pred.getEndSpan() == secondItem.getEndSpan()) {
                        hasRelation = true;
                        break;
                    }
                }
            }

            if (!hasRelation) {
                RelationMentionPair instance = new RelationMentionPair(firstItem, secondItem);
                RelationLabel structure = new RelationLabel(NO_RELATION_LABEL);

                sampledRelations.add(new Pair<>(instance, structure));
                addedEntities++;
                attempts = 2;
            }
         }

        return sampledRelations;
    }

    public static void annotateSLProblems(List<RelationMentionPair> slItems,
                                           SLModel trainedModel,
                                           PredicateArgumentView finalView) throws Exception {
        for (RelationMentionPair instance : slItems) {

            RelationLabel predictedStructure = (RelationLabel) trainedModel.infSolver.getBestStructure(
                    trainedModel.wv,
                    instance);

            Constituent predicate = instance.getFirstMention().cloneForNewViewWithDestinationLabel(
                    finalView.getViewName(),
                    predictedStructure.getRelationLabel());

            Constituent argument = instance.getSecondMention().cloneForNewViewWithDestinationLabel(
                    finalView.getViewName(),
                    predictedStructure.getRelationLabel());

            // Populate the final relation view.
            finalView.addPredicateArguments(
                    predicate,
                    Collections.singletonList(argument),
                    new String[] { predictedStructure.getRelationLabel() },
                    new double[] { 1.0f });
        }
    }
}
