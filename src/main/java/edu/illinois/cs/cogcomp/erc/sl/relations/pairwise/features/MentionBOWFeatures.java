package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.features;

import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationMentionPair;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationLabel;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.Arrays;

/**
 * @author Bhargav Mangipudi
 */
public class MentionBOWFeatures extends FeatureDefinitionBase {

    public MentionBOWFeatures(Lexiconer lm) {
        super(lm);
    }

    @Override
    public FeatureVectorBuffer getFeatureVector(RelationMentionPair instance, RelationLabel structure) {
        String nullFeature = this.featurePrefix + "_" + structure.getRelationLabel();
        String[] bowWordsFeatures = new String[2];


        String[] firstMentionUnordered = instance.getFirstMention().getSurfaceForm().split(" ");
        Arrays.sort(firstMentionUnordered);
        //noinspection Since15
        bowWordsFeatures[0] = String.join(" ", firstMentionUnordered);

        String[] secondMentionUnordered = instance.getSecondMention().getSurfaceForm().split(" ");
        Arrays.sort(secondMentionUnordered);
        //noinspection Since15
        bowWordsFeatures[1] = String.join(" ", secondMentionUnordered);

        if (this.lexiconer.isAllowNewFeatures()) {
            this.lexiconer.addFeature(nullFeature);
            for (String feat : bowWordsFeatures) {
                this.lexiconer.addFeature(nullFeature + "_" + feat);
            }
        }

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        for (String feat : bowWordsFeatures) {

            if (this.lexiconer.containFeature(nullFeature + "_" + feat)) {
                fvb.addFeature(this.lexiconer.getFeatureId(nullFeature + "_" + feat), 1.0f);
            } else {
                fvb.addFeature(this.lexiconer.getFeatureId(nullFeature), 1.0f);
            }
        }

        return fvb;
    }
}
