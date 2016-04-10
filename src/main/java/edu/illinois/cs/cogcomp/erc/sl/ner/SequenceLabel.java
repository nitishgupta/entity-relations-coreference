package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.sl.core.IStructure;

/**
 * Created by Bhargav Mangipudi on 3/8/16.
 */
public class SequenceLabel implements IStructure {
    //public final int[] tagIds;
    public final String[] labels;

//    public SequenceLabel(int[] tags) {
//        assert tags.length != 0 : "Sentence must have at least one label";
//
//        this.tagIds = tags;
//        labels = null;
//    }

    public SequenceLabel(String[] labels) {
        assert labels.length != 0 : "Sentence must have at least one label";

        this.labels = labels;
    }

    public String getLabelAtPosition(int i){
        if(i >= labels.length)
            return "";
        else
            return labels[i];
    }

    public String[] getLabels(){    return labels;  }

    @Override
    public String toString() {
        return "";
    }
}
