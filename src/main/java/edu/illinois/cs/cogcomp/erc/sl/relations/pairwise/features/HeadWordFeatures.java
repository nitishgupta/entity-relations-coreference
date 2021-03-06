package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.features;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.erc.ir.DocUtils;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationMentionPair;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationLabel;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bhargav Mangipudi
 */
public class HeadWordFeatures extends FeatureDefinitionBase {
    private static final Logger logger = LoggerFactory.getLogger(HeadWordFeatures.class);

    public HeadWordFeatures(Lexiconer lexiconer) {
        super(lexiconer);
    }

    @Override
    public FeatureVectorBuffer getFeatureVector(RelationMentionPair instance, RelationLabel structure) {
        String nullFeature = this.featurePrefix + "_" + structure.getRelationLabel();
        String[] headWordFeatures = new String[3];

        // Treat constituents as head words
        Constituent firstMentionHead = instance.getFirstMention();
        Constituent secondMentionHead = instance.getSecondMention();

        headWordFeatures[0] = firstMentionHead == null ? "" : firstMentionHead.getSurfaceForm();
        headWordFeatures[1] = secondMentionHead == null ? "" : secondMentionHead.getSurfaceForm();
        headWordFeatures[2] = headWordFeatures[0] + "_" + headWordFeatures[1];

        if (this.lexiconer.isAllowNewFeatures()) {
            this.lexiconer.addFeature(nullFeature);
            for (String feat : headWordFeatures) {
                this.lexiconer.addFeature(nullFeature + "_" + feat);
            }
        }

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        for (String feat : headWordFeatures) {

            if (this.lexiconer.containFeature(nullFeature + "_" + feat)) {
                fvb.addFeature(this.lexiconer.getFeatureId(nullFeature + "_" + feat), 1.0f);
            } else {
                fvb.addFeature(this.lexiconer.getFeatureId(nullFeature), 1.0f);
            }
        }

        return fvb;
    }
}
