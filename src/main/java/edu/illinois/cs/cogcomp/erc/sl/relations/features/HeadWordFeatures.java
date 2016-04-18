package edu.illinois.cs.cogcomp.erc.sl.relations.features;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.erc.ir.DocUtils;
import edu.illinois.cs.cogcomp.erc.sl.relations.SLInstance;
import edu.illinois.cs.cogcomp.erc.sl.relations.SLStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

/**
 * @author Bhargav Mangipudi
 */
public class HeadWordFeatures extends FeatureDefinitionBase {
    private String featurePrefix;

    public HeadWordFeatures(Lexiconer lexiconer) {
        super(lexiconer);

        this.featurePrefix = this.getClass().getName();
    }

    @Override
    public FeatureVectorBuffer getFeatureVector(SLInstance instance, SLStructure structure) {
        String nullFeature = this.featurePrefix + "_" + structure.getRelationLabel();
        String[] headWordFeatures = new String[3];

        Constituent firstMentionHead = DocUtils.getHeadConstituentForEntityExtent(instance.getFirstMention(), "View");
        Constituent secondMentionHead = DocUtils.getHeadConstituentForEntityExtent(instance.getSecondMention(), "View");

        // TODO: Add logging here.
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
