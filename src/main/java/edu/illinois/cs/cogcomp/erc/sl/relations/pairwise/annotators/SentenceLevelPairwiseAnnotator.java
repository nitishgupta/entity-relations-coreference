package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.annotators;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.QueryableList;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.ViewTypes;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.transformers.Predicate;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationMentionPair;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.SLHelper;
import edu.illinois.cs.cogcomp.erc.util.PipelineService;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public class SentenceLevelPairwiseAnnotator extends Annotator {

    private boolean is2004Document;
    private SLModel trainedModel;
    private String mentionView;

    private static Logger logger = LoggerFactory.getLogger(SentenceLevelPairwiseAnnotator.class);

    /**
     * Pairwise Relation Annotator which annotates relation that are present in each pair of mentions that are present
     * in a single sentence.
     *
     * @param finalViewName Name of the final predicted view.
     * @param requiredViews List of input view that are required to perform annotation.
     * @param inputMentionView Name of the gold relation view. (Optional here)
     * @param model SLModel that is used to classify.
     * @param is2004Document Boolean denoting if the annotator will work on ACE2004 document or not.
     */
    public SentenceLevelPairwiseAnnotator(String finalViewName,
                                          String[] requiredViews,
                                          String inputMentionView,
                                          SLModel model,
                                          boolean is2004Document) {
        super(finalViewName, requiredViews);

        this.trainedModel = model;
        this.is2004Document = is2004Document;
        this.mentionView = inputMentionView;
    }

    @Override
    public void addView(TextAnnotation textAnnotation) throws AnnotatorException {
        if (!textAnnotation.getAvailableViews().containsAll(Arrays.asList(this.requiredViews))) {
            logger.error("TA is missing some required views");
            return;
        }

        // Add required views
        PipelineService.addRequiredViews(textAnnotation);

        // Disable modification of lexicon while testing.
        this.trainedModel.lm.setAllowNewFeatures(false);

        List<RelationMentionPair> slItems = new ArrayList<>();

        SpanLabelView entityView = (SpanLabelView) textAnnotation.getView(mentionView);
        SpanLabelView sentenceView = (SpanLabelView) textAnnotation.getView(ViewNames.SENTENCE);

        // All Mentions in the entityView.
        IQueryable<Constituent> allMentions = new QueryableList<>(entityView.getConstituents());

        for(Constituent sentence : sentenceView.getConstituents()) {
            // Filtering mentions by sentence.
            IQueryable<Constituent> mentionsInSentence = allMentions.where(Queries.containedInConstituent(sentence));

            // Generate pairwise SLItems
            for (Constituent firstEntity : mentionsInSentence) {
                for (Constituent secondEntity : mentionsInSentence) {
                    if (firstEntity != secondEntity) {
                        slItems.add(new RelationMentionPair(firstEntity, secondEntity));
                    }
                }
            }
        }

        if (slItems == null) {
            logger.error("Error while populating SL Problem");
            return;
        }

        PredicateArgumentView finalView = new PredicateArgumentView(this.viewName, textAnnotation);

        try {
            // Annotate populated candidates.
            SLHelper.annotateSLProblems(slItems, trainedModel, finalView);
        } catch (Exception ex) {
            logger.error("Error while processing instance.", ex);
        }

        textAnnotation.addView(this.viewName, finalView);
    }
}
