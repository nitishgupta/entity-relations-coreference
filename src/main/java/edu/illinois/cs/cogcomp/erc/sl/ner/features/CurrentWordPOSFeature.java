package edu.illinois.cs.cogcomp.erc.sl.ner.features;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.erc.sl.ner.LexiconerConstants;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceLabel;
import edu.illinois.cs.cogcomp.erc.util.Utils;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.List;

/**
 * Created by Bhargav Mangipudi on 3/27/16.
 */
public class CurrentWordPOSFeature extends FeatureDefinitionBase {
    public CurrentWordPOSFeature(Lexiconer lm) {
        super(lm);
    }

    // [L1[POSs], L2[POSs], ...... , Ln[POSs]]
    @Override
    public FeatureVectorBuffer getSparseFeature(SequenceInstance sequence, SequenceLabel label) {
        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        List<String> posTagSequence = sequence.getPOSTagSequence();

        int idx = 0;
        for (Constituent c : sequence.getConstituents()) {
            int labelId = label.tagIds[idx];

            String posTag = posTagSequence.get(idx);
            int posTagFeatureId = Utils.getFeatureIdOrElse(
                    this.lexiconer,
                    LexiconerConstants.POS_PREFIX + posTag,
                    LexiconerConstants.POS_UNKNOWN);

            fvb.addFeature(this.lexiconer.getNumOfFeature() * labelId + posTagFeatureId, 1.0f);
            idx++;
        }

        return fvb;
    }

    @Override
    public int getFeatureSize() {
        return this.lexiconer.getNumOfLabels() * this.lexiconer.getNumOfFeature();
    }

    @Override
    public FeatureVectorBuffer getLocalScore(SequenceInstance sequence,
                                             int currentWordPosition,
                                             int prevLabelId,
                                             int currentLabelId) {
        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        String posTag = sequence.getPOSTagSequence().get(currentWordPosition);
        int posTagFeatureId = Utils.getFeatureIdOrElse(
                this.lexiconer,
                LexiconerConstants.POS_PREFIX + posTag,
                LexiconerConstants.POS_UNKNOWN);

        fvb.addFeature(this.lexiconer.getNumOfFeature() * currentLabelId +  posTagFeatureId, 1.0f);

        return fvb;
    }
}
