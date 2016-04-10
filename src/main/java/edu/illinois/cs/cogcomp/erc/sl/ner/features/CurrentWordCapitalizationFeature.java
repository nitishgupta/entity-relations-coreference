//package edu.illinois.cs.cogcomp.erc.sl.ner.features;
//
//import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
//import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
//import edu.illinois.cs.cogcomp.edison.features.helpers.WordHelpers;
//import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
//import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceLabel;
//import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
//import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
//
///**
// * Created by Bhargav Mangipudi on 4/7/16.
// */
//public class CurrentWordCapitalizationFeature extends FeatureDefinitionBase {
//    public CurrentWordCapitalizationFeature(Lexiconer lm) {
//        super(lm);
//    }
//
//
//    @Override
//    public FeatureVectorBuffer getSparseFeature(SequenceInstance sequence, SequenceLabel label) {
//        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
//
//        TextAnnotation ta = sequence.getConstituents().get(0).getTextAnnotation();
//        int idx = 0;
//        for (Constituent c : sequence.getConstituents()) {
//            boolean isCapitalized = WordHelpers.isCapitalized(ta, ta.getTokenIdFromCharacterOffset(c.getStartCharOffset()));
//            if (isCapitalized) {
//                fvb.addFeature(label.tagIds[idx], 1.0f);
//            }
//            idx++;
//        }
//
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
//        Constituent c = sequence.getConstituents().get(currentWordPosition);
//
//        TextAnnotation ta = c.getTextAnnotation();
//        boolean isCapitalized = WordHelpers.isCapitalized(ta, ta.getTokenIdFromCharacterOffset(c.getStartCharOffset()));
//
//        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
//        if (isCapitalized) {
//            fvb.addFeature(currentLabelId, 1.0f);
//        }
//
//        return fvb;
//    }
//}
