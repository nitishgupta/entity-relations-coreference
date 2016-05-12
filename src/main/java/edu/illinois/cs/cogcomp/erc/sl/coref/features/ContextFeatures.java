package edu.illinois.cs.cogcomp.erc.sl.coref.features;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.sl.coref.CorefLabel;
import edu.illinois.cs.cogcomp.erc.sl.coref.CorefMentionPair;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import scala.collection.immutable.Stream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitishgupta on 5/11/16.
 */
public class ContextFeatures extends FeatureDefinitionBase {
    public ContextFeatures(Lexiconer lm) {
        super(lm);
    }

    public static List<String> getContextWords(Constituent c){
        int contextWindowSize = 3;
        TextAnnotation ta = c.getTextAnnotation();
        int numWords = ta.getTokens().length;
        int start1 = c.getStartSpan();
        int end1 = c.getEndSpan();
        int sc = Math.max(start1 - contextWindowSize, 0);
        int ec = Math.min(end1 + contextWindowSize, numWords - 1);
        List<String> contextWords = new ArrayList<String>();
        for(int i = sc; i<ec; i++){
            if(i < start1 || i > end1){
                String w = ta.getToken(i);
                contextWords.add(w);
            }
        }

        return contextWords;
    }

    // The structure will not be used : Solving a binary classification problem
    @Override
    public FeatureVectorBuffer getFeatureVector(CorefMentionPair instance, CorefLabel structure) {
        Constituent c1 = instance.getFirstConstituent();
        Constituent c2 = instance.getSecondConstituent();

        String featurePrefix = this.featurePrefix;
        List<String> context1 = getContextWords(c1);
        List<String> context2 = getContextWords(c2);

        List<String> features = new ArrayList<>();

        for(String c : context1)
            features.add(this.featurePrefix + "_head1_" + c);

        for(String c : context1)
            features.add(this.featurePrefix + "_head2_" + c);

        for(String c : context2)
            features.add(this.featurePrefix + "_head1_" + c);

        for(String c : context2)
            features.add(this.featurePrefix + "_head2_" + c);

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        if (this.lexiconer.isAllowNewFeatures()) {
            // Add the NULL feature equivalent
            //this.lexiconer.addFeature(featurePrefix);
            for(String f : features) {
                this.lexiconer.addFeature(f);
            }
        }

        for(String f : features){
            if (this.lexiconer.containFeature(f)) {
                fvb.addFeature(this.lexiconer.getFeatureId(f), 1.0f);
            }
        }

        return fvb;
    }
}
