package edu.illinois.cs.cogcomp.erc.sl.coref;

import edu.illinois.cs.cogcomp.erc.sl.coref.features.FeatureDefinitionBase;
import edu.illinois.cs.cogcomp.erc.sl.coref.features.HeadSurfaceFeature;
import edu.illinois.cs.cogcomp.erc.sl.coref.features.MentionTypeFeature;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nitishgupta on 5/10/16.
 */
public class FeatureGenerator extends FeatureDefinitionBase implements Serializable {
    public List<FeatureDefinitionBase> activeFeatures;

    public FeatureGenerator(Lexiconer lm) {
        super(lm);

        List<FeatureDefinitionBase> activeFeatureList = new ArrayList<>();

        // List of active features to be used.
        //activeFeatureList.add(new MentionTypeFeature(lm));
        activeFeatureList.add(new HeadSurfaceFeature(lm));
        //activeFeatureList.add(new HeadWordFeatures(lm));
        //activeFeatureList.add(new MentionBOWFeatures(lm));

        this.activeFeatures = Collections.unmodifiableList(activeFeatureList);
    }

    @Override
    public FeatureVectorBuffer getFeatureVector(CorefMentionPair instance, CorefLabel structure) {
        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        for (FeatureDefinitionBase feature : this.activeFeatures) {
            fvb.addFeature(feature.getFeatureVector(instance, structure));
        }

        return fvb;
    }
}

