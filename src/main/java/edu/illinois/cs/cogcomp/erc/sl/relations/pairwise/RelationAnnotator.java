package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public class RelationAnnotator extends Annotator {

    private boolean is2004Document;
    private SLModel trainedModel;
    private String inputRelationView;

    private static Logger logger = LoggerFactory.getLogger(RelationAnnotator.class);

    public RelationAnnotator(String finalViewName,
                             String[] requiredViews,
                             String inputRelationView,
                             SLModel model,
                             boolean is2004Document) {
        super(finalViewName, requiredViews);

        this.trainedModel = model;
        this.is2004Document = is2004Document;
        this.inputRelationView = inputRelationView;
    }

    @Override
    public void addView(TextAnnotation textAnnotation) throws AnnotatorException {
        if (!textAnnotation.getAvailableViews().containsAll(Arrays.asList(this.requiredViews))) {
            logger.error("TA is missing some required views");
            return;
        }

        PredicateArgumentView finalView = new PredicateArgumentView(this.viewName, textAnnotation);
        Document doc = new Document(textAnnotation, is2004Document, textAnnotation.getId());

        // Disable modification of lexicon while testing.
        this.trainedModel.lm.setAllowNewFeatures(false);

        List<Pair<RelationMentionPair, RelationLabel>> slItems = SLHelper.populateSLProblemForDocument(
                doc,
                this.trainedModel.lm,
                ViewNames.NER_ACE_COARSE, // TODO: Fix this.
                this.inputRelationView);

        if (slItems == null) {
            logger.error("Error while populating SL Problem");
            return;
        }

        for (Pair<RelationMentionPair, RelationLabel> problemInstance : slItems) {
            RelationMentionPair instance = problemInstance.getFirst();

            try {
                RelationLabel predictedStructure = (RelationLabel) this.trainedModel.infSolver.getBestStructure(
                        this.trainedModel.wv,
                        instance);

                Constituent predicate = instance.getFirstMention().cloneForNewViewWithDestinationLabel(
                        this.viewName,
                        predictedStructure.getRelationLabel());

                Constituent argument = instance.getSecondMention().cloneForNewViewWithDestinationLabel(
                        this.viewName,
                        predictedStructure.getRelationLabel());

                // Populate the final relation view.
                finalView.addPredicateArguments(
                        predicate,
                        Collections.singletonList(argument),
                        new String[] { predictedStructure.getRelationLabel() },
                        new double[] { 1.0f });
            } catch (Exception ex) {
                logger.error("Error while processing instance.", ex);
            }
        }

        textAnnotation.addView(this.viewName, finalView);
    }
}
