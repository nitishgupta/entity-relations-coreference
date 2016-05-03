package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise;

import edu.illinois.cs.cogcomp.sl.core.IStructure;

/**
 * @author Bhargav Mangipudi
 */
public class RelationLabel implements IStructure {
    private String relationLabel;

    public RelationLabel(String relationLabel) {
        this.relationLabel = relationLabel;
    }

    public String getRelationLabel() {
        return this.relationLabel;
    }
}
