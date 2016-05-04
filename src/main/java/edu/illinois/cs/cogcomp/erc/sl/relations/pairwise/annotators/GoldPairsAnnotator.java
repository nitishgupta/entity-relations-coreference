package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.annotators;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationLabel;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationMentionPair;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.SLHelper;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public class GoldPairsAnnotator extends Annotator {
    private boolean is2004Document;
    private SLModel trainedModel;
    private String inputRelationView;

    private static Logger logger = LoggerFactory.getLogger(GoldPairsAnnotator.class);

    /**
     * Pairwise Relation Annotator which annotates relation that corresponding to gold relations.
     * This annotator uses gold mention boundaries and gold relation inputs.
     *
     * @param finalViewName Name of the final predicted view.
     * @param requiredViews List of input view that are required to perform annotation.
     * @param inputRelationView Name of the gold relation view. Used to get evaluation candidates.
     * @param model SLModel that is used to classify.
     * @param is2004Document Boolean denoting if the annotator will work on ACE2004 document or not.
     */
    public GoldPairsAnnotator(String finalViewName,
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
                Parameters.RELATION_PAIRWISE_MENTION_VIEW_GOLD,
                this.inputRelationView);

        List<RelationMentionPair> instances = new ArrayList<>(slItems.size());
        for (Pair<RelationMentionPair, RelationLabel> pair : slItems) {
            instances.add(pair.getFirst());
        }

        if (slItems == null) {
            logger.error("Error while populating SL Problem");
            return;
        }

        try {
            SLHelper.annotateSLProblems(instances, this.trainedModel, finalView);
        } catch (Exception ex) {
            logger.error("Error while processing instance.", ex);
        }

        textAnnotation.addView(this.viewName, finalView);
    }
}
