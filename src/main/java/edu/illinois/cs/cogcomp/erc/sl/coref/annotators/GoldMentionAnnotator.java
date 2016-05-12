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

import java.util.*;

/**
 * Created by nitishgupta on 5/11/16.
 */
public class GoldMentionAnnotator extends Annotator {
    private boolean is2004Document;
    private SLModel trainedModel;
    private String entityViewName;

    private static Logger logger = LoggerFactory.getLogger(GoldMentionAnnotator.class);

    public GoldMentionAnnotator(String finalViewName,
                              String[] requiredViews,
                              SLModel model,
                              String entityViewName,
                              boolean is2004Document) {
        super(finalViewName, requiredViews);
        this.entityViewName = entityViewName;
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

        //List<Constituent> mentions = textAnnotation.getView(Parameters.COREF_MENTION_VIEW_GOLD).getConstituents();
        List<Constituent> mentions = textAnnotation.getView(entityViewName).getConstituents();
        mentions = SLHelper.sortIncreasing(mentions);
        int nummentions = mentions.size();

        String clusterPrefix = "clusterID-";
        int clusterId = 0;

        List<Constituent> coref_mentions = new ArrayList<Constituent>();
        Map<String, List<Constituent>> clusterId_ConsMap = new HashMap<>();
        for(int i = 0; i < nummentions; i++){
            Constituent currC = mentions.get(i);
            int past = i-1, best = -1; double bestScore = Double.MIN_VALUE;
            while(past >= 0){
                Constituent prevC = coref_mentions.get(past);
                CorefMentionPair CE = new CorefMentionPair(currC, prevC);
                double t_score = getPOSScore(CE);
                double f_score = getNEGScore(CE);
                //System.out.println(t_score + "\t" + f_score);
                if((t_score > f_score) && (t_score > bestScore)){
                    bestScore = t_score;
                    best = past;
                }
                past--;
            }

            if(best != -1 && bestScore > 0.0){
                String clusterLabel = coref_mentions.get(best).getLabel();
                Constituent newCorefC = currC.cloneForNewViewWithDestinationLabel(this.viewName, clusterLabel);
                clusterId_ConsMap.get(clusterLabel).add(newCorefC);
                coref_mentions.add(newCorefC);
            } else{
                String newClusterID = clusterPrefix+clusterId;
                Constituent newCorefC = currC.cloneForNewViewWithDestinationLabel(this.viewName, newClusterID);
                coref_mentions.add(newCorefC);
                clusterId_ConsMap.put(newClusterID, new ArrayList<Constituent>());
                clusterId_ConsMap.get(newClusterID).add(newCorefC);
                clusterId++;
            }
        }

        for(String clusterID : clusterId_ConsMap.keySet()){
            List<Constituent> corefmentions = clusterId_ConsMap.get(clusterID);
            Constituent cano = null; int length = -1;
            for(Constituent c : corefmentions){
                if(c.getSurfaceForm().length() > length)
                    cano = c;
            }
            finalView.addCorefEdges(cano, corefmentions);
        }



//        for(Constituent m : coref_mentions)
//            finalView.addConstituent(m);


        textAnnotation.addView(this.viewName, finalView);
    }

    public double getNEGScore(CorefMentionPair CExample){
        IFeatureVector f_fv_link = trainedModel.featureGenerator.getFeatureVector((IInstance) CExample, new CorefLabel(CorefLabel.f));
        double fscore = trainedModel.wv.dotProduct(f_fv_link);
        return fscore;

    }

    public double getPOSScore(CorefMentionPair CExample){
        IFeatureVector t_fv_link = trainedModel.featureGenerator.getFeatureVector((IInstance) CExample, new CorefLabel(CorefLabel.t));
        double tscore = trainedModel.wv.dotProduct(t_fv_link);
        return tscore;

    }
}
