//package edu.illinois.cs.cogcomp.erc.sl.ner.features;
//
//import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
//import edu.illinois.cs.cogcomp.erc.sl.ner.LexiconerConstants;
//import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
//import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceLabel;
//import edu.illinois.cs.cogcomp.erc.util.Utils;
//import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
//import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
//
///**
// * Created by Bhargav Mangipudi on 3/8/16.
// */
//public class EmissionFeatures extends FeatureDefinitionBase {
//    public EmissionFeatures(Lexiconer lm) {
//        super(lm);
//    }
//
//    // [L1[Words], L2[Words], ...... , Ln[Words]]
//    @Override
//    public FeatureVectorBuffer getSparseFeature(SequenceInstance sequence, SequenceLabel label) {
//        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
//
//        int idx = 0;
//        for (Constituent c : sequence.getConstituents()) {
//            int featureId = Utils.getFeatureIdOrElse(
//                    this.lexiconer,
//                    LexiconerConstants.WORD_PREFIX + c.getSurfaceForm(),
//                    LexiconerConstants.WORD_UNKNOWN);
//
//            fvb.addFeature(featureId + this.lexiconer.getNumOfFeature() * label.tagIds[idx], 1.0f);
//            idx++;
//        }
//        return fvb;
//    }
//
//    @Override
//    public int getFeatureSize() {
//        return this.lexiconer.getNumOfFeature() * this.lexiconer.getNumOfLabels();
//    }
//
//    @Override
//    public FeatureVectorBuffer getLocalScore(SequenceInstance sequence,
//                                             int currentWordPosition,
//                                             int prevLabelId,
//                                             int currentLabelId) {
//        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
//
//        Constituent c = sequence.getConstituents().get(currentWordPosition);
//        int featureId = Utils.getFeatureIdOrElse(
//                this.lexiconer,
//                LexiconerConstants.WORD_PREFIX + c.getSurfaceForm(),
//                LexiconerConstants.WORD_UNKNOWN);
//
//        fvb.addFeature(featureId + this.lexiconer.getNumOfFeature() * currentLabelId, 1.0f);
//
//        return fvb;
//    }
//}
