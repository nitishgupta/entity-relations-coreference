package edu.illinois.cs.cogcomp.erc.sl.coref.annotators;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.sl.coref.CorefLabel;
import edu.illinois.cs.cogcomp.erc.sl.coref.CorefMentionPair;
import edu.illinois.cs.cogcomp.erc.sl.coref.SLHelper;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nitishgupta on 5/11/16.
 */
public class GoldMentionAnnotator extends Annotator {
    private boolean is2004Document;
    private SLModel trainedModel;

    private static Logger logger = LoggerFactory.getLogger(GoldMentionAnnotator.class);

    public GoldMentionAnnotator(String finalViewName,
                              String[] requiredViews,
                              SLModel model,
                              boolean is2004Document) {
        super(finalViewName, requiredViews);

        this.trainedModel = model;
        this.is2004Document = is2004Document;
    }

    @Override
    public void addView(TextAnnotation textAnnotation) throws AnnotatorException {
        if (!textAnnotation.getAvailableViews().containsAll(Arrays.asList(this.requiredViews))) {
            logger.error("TA is missing some required views");
            return;
        }

        CoreferenceView finalView = new CoreferenceView(this.viewName, textAnnotation);
        Document doc = new Document(textAnnotation, is2004Document, textAnnotation.getId());

        // Disable modification of lexicon while testing.
        this.trainedModel.lm.setAllowNewFeatures(false);

        List<Constituent> mentions = textAnnotation.getView(Parameters.COREF_MENTION_VIEW_GOLD).getConstituents();
        mentions = SLHelper.sortIncreasing(mentions);
        int nummentions = mentions.size();

        String clusterPrefix = "clusterID-";
        int clusterId = 0;

        List<Constituent> coref_mentions = new ArrayList<Constituent>();
        for(int i = 0; i < nummentions; i++){
            Constituent currC = mentions.get(i);
            int past = i-1, best = -1; double bestScore = Double.MIN_VALUE;
            while(past >= 0){
                Constituent prevC = coref_mentions.get(past);
                CorefMentionPair CE = new CorefMentionPair(currC, prevC);
                double score = getScore(CE);
                if(score > bestScore){
                    bestScore = score;
                    best = past;
                }
                past--;
            }

            if(best == -1){
                Constituent newCorefC = currC.cloneForNewViewWithDestinationLabel(this.viewName, clusterPrefix+clusterId);
                coref_mentions.add(newCorefC);
                clusterId++;
            } else{
                String clusterLabel = coref_mentions.get(best).getLabel();
                Constituent newCorefC = currC.cloneForNewViewWithDestinationLabel(this.viewName, clusterLabel);
                coref_mentions.add(newCorefC);
            }
        }
        for(Constituent m : coref_mentions)
            finalView.addConstituent(m);


        textAnnotation.addView(this.viewName, finalView);
    }

    public double getScore(CorefMentionPair CExample){
        IFeatureVector fv_link = trainedModel.featureGenerator.getFeatureVector((IInstance) CExample, new CorefLabel("NULL"));
        return trainedModel.wv.dotProduct(fv_link);
    }
}
