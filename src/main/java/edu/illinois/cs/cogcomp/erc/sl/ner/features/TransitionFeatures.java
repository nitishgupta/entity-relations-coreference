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
//public class TransitionFeatures extends FeatureDefinitionBase {
//    public TransitionFeatures(Lexiconer lm) {
//        super(lm);
//    }
//
//    @Override
//    public FeatureVectorBuffer getSparseFeature(SequenceInstance sequence, SequenceLabel label) {
//        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
//
//        int numTags = label.labels.length;
//
//        for (int i = 1; i < numTags; i++) {
//            fvb.addFeature(label.tagIds[i-1] * this.lexiconer.getNumOfLabels() + label.tagIds[i], 1.0f);
//        }
//
//        return fvb;
//    }
//
//    @Override
//    public int getFeatureSize() {
//        return this.lexiconer.getNumOfLabels() * this.lexiconer.getNumOfLabels();
//    }
//
//    @Override
//    public FeatureVectorBuffer getLocalScore(SequenceInstance sequence,
//                                             int currentWordPosition,
//                                             int prevLabelId,
//                                             int currentLabelId) {
//        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
//
//        if (currentWordPosition > 0) {
//            fvb.addFeature(prevLabelId * this.lexiconer.getNumOfLabels() + currentLabelId, 1.0f);
//        }
//
//        return fvb;
//    }
//}
