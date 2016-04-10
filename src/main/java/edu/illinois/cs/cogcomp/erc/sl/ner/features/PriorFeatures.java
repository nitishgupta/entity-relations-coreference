//package edu.illinois.cs.cogcomp.erc.sl.ner.features;
//
//import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
//import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceLabel;
//import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
//import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
//
///**
// * Created by Bhargav Mangipudi on 3/8/16.
// */
//public class PriorFeatures extends FeatureDefinitionBase {
//    public PriorFeatures(Lexiconer lm) {
//        super(lm);
//    }
//
//    @Override
//    public FeatureVectorBuffer getSparseFeature(SequenceInstance sequence, SequenceLabel label) {
//        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
//        fvb.addFeature(label.tagIds[0], 1.0f);
//        return fvb;
//    }
//
//    @Override
//    public int getFeatureSize() {
//        return this.lexiconer.getNumOfLabels();
//    }
//
//    @Override
//    public FeatureVectorBuffer getLocalScore(SequenceInstance sequence,
//                                             int currentWordPosition,
//                                             int prevLabelId,
//                                             int currentLabelId) {
//        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
//
//        // Prior features are will not have a value for non-first position
//        if (currentWordPosition == 0)
//            fvb.addFeature(currentLabelId, 1.0f);
//
//        return fvb;
//    }
//}
