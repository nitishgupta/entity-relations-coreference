package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.features.helpers.WordHelpers;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 3/8/16.
 */
public class FeatureGenerator extends AbstractFeatureGenerator implements Serializable {
    private static final long serialVersionUID = 8246812640996043593L;
    private Lexiconer lm;
    //List<FeatureDefinitionBase> activeFeatures;

    /**
     * @param lm : Lexiconer to add features into
     */
    public FeatureGenerator(Lexiconer lm) {
        this.lm = lm;
    }

    @Override
    public IFeatureVector getFeatureVector(IInstance iInstance, IStructure iStructure) {
        SequenceInstance sequence = (SequenceInstance)iInstance;
        SequenceLabel label = (SequenceLabel)iStructure;
        FeatureVectorBuffer fvb = getSparseFeature(sequence, label);
        return fvb.toFeatureVector();
    }


    public FeatureVectorBuffer getSparseFeature(SequenceInstance sequence, SequenceLabel label) {
        int shift = 0;
        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
        for(int i=0; i<sequence.size(); i++){
            FeatureVectorBuffer fvbi = getFeaturesForSentenceAtPosition(sequence, label, i);
            fvb.addFeature(fvbi);
        }

        return fvb;
    }

    public FeatureVectorBuffer getFeaturesForSentenceAtPosition(SequenceInstance sent, SequenceLabel labelseq, int i) {
        List<String> featureMap = new ArrayList<>();

        addPriorFeature(sent, labelseq, i, featureMap);
        addEmissionFeature(sent, labelseq, i, featureMap);
        addPOSEmissionFeature(sent, labelseq, i, featureMap);
        addCapitalizationFeature(sent, labelseq, i, featureMap);
        addLabelTransitionFeature(labelseq, i, featureMap);

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
        for (String f : featureMap) {
            if (lm.isAllowNewFeatures()) {
                lm.addFeature(f);
            }

            if (lm.containFeature(f)) {
                fvb.addFeature(lm.getFeatureId(f), 1.0f);
            } else {
//                fvb.addFeature(lm.getFeatureId(LexiconerConstants.WORD_UNKNOWN), 1.0f);
            }
        }
        return fvb;
    }

    private void addPriorFeature(SequenceInstance sent, SequenceLabel label, int i,
                                    List<String> featureMap) {
        if(i == 0)
            featureMap.add(LexiconerConstants.LABEL_PREFIX + label.getLabelAtPosition(i) + "_Prior");
    }

    private void addEmissionFeature(SequenceInstance sent, SequenceLabel label, int i,
                                    List<String> featureMap) {
        featureMap.add(LexiconerConstants.WORD_PREFIX + sent.getTokenAtPosition(i)
                + "_"
                + LexiconerConstants.LABEL_PREFIX + label.getLabelAtPosition(i) + "_WordEmmision");
    }

    private void addPOSEmissionFeature(SequenceInstance sent, SequenceLabel label, int i,
                                    List<String> featureMap) {
        featureMap.add(LexiconerConstants.POS_PREFIX + sent.getPOSAtPosition(i)
                + "_"
                + LexiconerConstants.LABEL_PREFIX + label.getLabelAtPosition(i) + "_POSEmmision");
    }

    private void addCapitalizationFeature(SequenceInstance sent, SequenceLabel label, int i,
                                    List<String> featureMap) {
        Constituent c = sent.getConstituents().get(i);
        TextAnnotation ta = c.getTextAnnotation();
        boolean isCapitalized = WordHelpers.isCapitalized(ta, ta.getTokenIdFromCharacterOffset(c.getStartCharOffset()));
        if(isCapitalized)
            featureMap.add(LexiconerConstants.LABEL_PREFIX + label.getLabelAtPosition(i) + "_Capital");
    }

    private void addLabelTransitionFeature(SequenceLabel label, int i,
                                          List<String> featureMap) {
        if(i >= 1){
            featureMap.add(LexiconerConstants.LABEL_PREFIX + label.getLabelAtPosition(i - 1)
                    + "_"
                    + LexiconerConstants.LABEL_PREFIX + label.getLabelAtPosition(i) + "_LabelTransition");
        }
    }

    public FeatureVectorBuffer  getLocalFeatureVector(SequenceInstance sent, String currentLabel, String prevLabel, int i) {
        List<String> featureMap = new ArrayList<>();

        addLocalPriorFeature(sent, currentLabel, prevLabel, i, featureMap);
        addLocalEmissionFeature(sent, currentLabel, prevLabel, i, featureMap);
        addLocalPOSEmissionFeature(sent, currentLabel, prevLabel, i, featureMap);
        addLocalCapitalizationFeature(sent, currentLabel, prevLabel, i, featureMap);
        addLocalLabelTransitionFeature(sent, currentLabel, prevLabel, i, featureMap);

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
        for (String f : featureMap) {
            if (lm.isAllowNewFeatures()) {
                lm.addFeature(f);
            }

            if (lm.containFeature(f)) {
                fvb.addFeature(lm.getFeatureId(f), 1.0f);
            } else {
//                fvb.addFeature(lm.getFeatureId(LexiconerConstants.WORD_UNKNOWN), 1.0f);
            }
        }

        return fvb;
    }

    private void addLocalPriorFeature(SequenceInstance sent,
                                      String currentLabel,
                                      String prevLabel,
                                      int i,
                                      List<String> featureMap) {
        if(i == 0)
            featureMap.add(currentLabel + "_Prior");
    }

    private void addLocalEmissionFeature(SequenceInstance sent,
                                         String currentLabel,
                                         String prevLabel,
                                         int i,
                                         List<String> featureMap) {
        featureMap.add(LexiconerConstants.WORD_PREFIX + sent.getTokenAtPosition(i)
                + "_" + currentLabel + "_WordEmmision");
    }

    private void addLocalPOSEmissionFeature(SequenceInstance sent,
                                            String currentLabel,
                                            String prevLabel,
                                            int i,
                                            List<String> featureMap) {
        featureMap.add(LexiconerConstants.POS_PREFIX + sent.getPOSAtPosition(i)
                + "_" + currentLabel + "_POSEmmision");
    }

    private void addLocalCapitalizationFeature(SequenceInstance sent,
                                               String currentLabel,
                                               String prevLabel,
                                               int i,
                                               List<String> featureMap) {
        Constituent c = sent.getConstituents().get(i);
        TextAnnotation ta = c.getTextAnnotation();
        boolean isCapitalized = WordHelpers.isCapitalized(ta, ta.getTokenIdFromCharacterOffset(c.getStartCharOffset()));
        if(isCapitalized)
            featureMap.add(currentLabel + "_Capital");
    }

    // If i == 0 then prevLabel = "" is input. This case is handled by the prior features
    private void addLocalLabelTransitionFeature(SequenceInstance sent,
                                                String currentLabel,
                                                String prevLabel,
                                                int i,
                                                List<String> featureMap) {
        if(i > 0){
            featureMap.add(prevLabel + "_" + currentLabel + "_LabelTransition");
        }
    }


//    /**
//     * Returns the size of the final feature vector.
//     * @return Size of the final FeatureVector
//     */
//    @Override
//    public int getFeatureSize() {
//        int size = 0;
//
//        for (FeatureDefinitionBase feature : this.activeFeatures) {
//            size += feature.getFeatureSize();
//        }
//
//        return size;
//    }
//
//
//    public FeatureVectorBuffer getLocalScore(SequenceInstance sequence,
//                                             int currentWordPosition,
//                                             int prevLabelId,
//                                             int currentLabelId) {
//        int shift = 0;
//        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
//
//        for (FeatureDefinitionBase feature : this.activeFeatures) {
//            fvb.addFeature(feature.getLocalScore(sequence, currentWordPosition, prevLabelId, currentLabelId), shift);
//            shift += feature.getFeatureSize();
//        }
//
//        return fvb;
//    }


//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Features Used for NER\n");
//
//        for (FeatureDefinitionBase feature : this.activeFeatures) {
//            sb.append(feature.getClass().getSimpleName() + ": " + feature.getFeatureSize() + "\n");
//        }
//
//        return sb.toString();
//    }
}
