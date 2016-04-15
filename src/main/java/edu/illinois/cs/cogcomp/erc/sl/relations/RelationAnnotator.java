package edu.illinois.cs.cogcomp.erc.sl.relations;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.ir.Document;
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

        List<Pair<SLInstance, SLStructure>> slItems = SLHelper.populateSLProblemForDocument(
                doc,
                this.inputRelationView,
                this.trainedModel.lm);

        if (slItems == null) {
            logger.error("Error while populating SL Problem");
            return;
        }

        for (Pair<SLInstance, SLStructure> problemInstance : slItems) {
            SLInstance instance = problemInstance.getFirst();

            try {
                SLStructure predictedStructure = (SLStructure) this.trainedModel.infSolver.getBestStructure(
                        this.trainedModel.wv,
                        instance);

                for (Relation rel : predictedStructure.getRelations()) {
                    Constituent predicate = rel.getSource();
                    Constituent argument = rel.getTarget();

                    // Populate the final relation view.
                    finalView.addPredicateArguments(
                            predicate,
                            Collections.singletonList(argument),
                            new String[] { rel.getRelationName() },
                            new double[] { rel.getScore() });
                }
            } catch (Exception ex) {
                logger.error("Error while processing instance.", ex);
            }
        }

        textAnnotation.addView(this.viewName, finalView);
    }
}
