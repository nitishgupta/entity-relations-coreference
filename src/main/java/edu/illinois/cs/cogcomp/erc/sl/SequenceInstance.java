package edu.illinois.cs.cogcomp.erc.sl;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

import java.util.Collections;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 3/7/16.
 */
public class SequenceInstance implements IInstance {
    List<Constituent> constituents;
    final int hashCode;

    public SequenceInstance(List<Constituent> constituentList) {
        assert constituentList.size() != 0;
        this.constituents = Collections.unmodifiableList(constituentList);

        int hashCodeVal = 0;
        for (Constituent c : this.constituents) {
            hashCodeVal += c.hashCode()*19; // Verify
        }

        this.hashCode = hashCodeVal;
    }

    public List<Constituent> getConstituents() { return this.constituents; }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
