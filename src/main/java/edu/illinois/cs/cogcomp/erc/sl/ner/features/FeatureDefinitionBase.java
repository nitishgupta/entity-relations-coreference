package edu.illinois.cs.cogcomp.erc.sl.ner.features;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceLabel;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;

import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public abstract class FeatureDefinitionBase extends AbstractFeatureGenerator implements Serializable {
    protected final Lexiconer lexiconer;
    public final String featurePrefix;

    public FeatureDefinitionBase(Lexiconer lm) {
        this.lexiconer = lm;
        this.featurePrefix = this.getClass().getSimpleName();
    }

    public void preExtractFeatures(SequenceInstance instance, SequenceLabel label) {
        // Ideally this should be better.
        this.getFeatureVector(instance, label);
    }

    public abstract void addLocalFeature(
            List<Pair<String, String>> featureMap,
            SequenceInstance sentence,
            String currentLabel,
            String prevLabel,
            int position);

    public IFeatureVector getFeatureVector(IInstance x, IStructure y) {
        SequenceInstance ins = (SequenceInstance) x;
        SequenceLabel sl = (SequenceLabel) y;

        return this.getFeatureVector(ins, sl).toFeatureVector();
    }

    public FeatureVectorBuffer getLocalFeatureVector(SequenceInstance sentence, String currentLabel, String prevLabel, int pos) {
        FeatureVectorBuffer fvb = new FeatureVectorBuffer();
        List<Pair<String, String>> featureMap = new ArrayList<>();

        this.addLocalFeature(featureMap, sentence, currentLabel, prevLabel, pos);

        for (Pair<String, String> feat : featureMap) {
            String f = this.featurePrefix + "_" + feat.getFirst() + "_" + feat.getSecond();
            String nullFeature = this.featurePrefix + "_" + feat.getSecond();

            if (this.lexiconer.isAllowNewFeatures()) {
                this.lexiconer.addFeature(f);
                this.lexiconer.addFeature(nullFeature);
            }

            if (this.lexiconer.containFeature(f)) {
                fvb.addFeature(this.lexiconer.getFeatureId(f), 1.0f);
            } else if (this.lexiconer.containFeature(nullFeature)){
                fvb.addFeature(this.lexiconer.getFeatureId(nullFeature), 1.0f);
            }
        }

        return fvb;
    }

    public FeatureVectorBuffer getFeatureVector(SequenceInstance ins, SequenceLabel sl) {
        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        for (int i = 0; i < ins.getConstituents().size(); i++) {
            String prevLabel = (i == 0) ? "" : sl.getLabelAtPosition(i - 1);
            String currentLabel = sl.getLabelAtPosition(i);

            fvb.addFeature(this.getLocalFeatureVector(ins, currentLabel, prevLabel, i));
        }

        return fvb;
    }
}
