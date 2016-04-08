package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

import java.util.Collections;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 3/7/16.
 *
 * The sequence instance is one input sentence. For NER it is a list of constituents from the NER_GOLD_BIO_VIEW for a sentence
 * Hence from each part of the sequence (token) the surface form (along with other data) can be extracted
 *
 */
public class SequenceInstance implements IInstance {

    List<Constituent> constituents;
    List<String> posTagSequence;

    final int hashCode;

    public SequenceInstance(List<Constituent> constituentList, List<String> posTagSequence) {
        assert constituentList.size() != 0;
        assert posTagSequence.size() == constituentList.size();

        this.constituents = Collections.unmodifiableList(constituentList);
        this.posTagSequence = Collections.unmodifiableList(posTagSequence);

        int hashCodeVal = 0;
        for (Constituent c : this.constituents) {
            hashCodeVal += c.hashCode()*19; // Verify
        }

        this.hashCode = hashCodeVal;
    }

    public List<Constituent> getConstituents() { return this.constituents; }

    public List<String> getPOSTagSequence() { return  this.posTagSequence; }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
