package edu.illinois.cs.cogcomp.erc.sl;

import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

/**
 * Created by Bhargav Mangipudi on 3/8/16.
 */
public class SequenceLabel implements IStructure {
    public final int[] tagIds;

    public SequenceLabel(int[] tags) {
        assert tags.length != 0 : "Sentence must have at least one label";

        this.tagIds = tags;
    }

    @Override
    public String toString() {
        return "";
    }
}
