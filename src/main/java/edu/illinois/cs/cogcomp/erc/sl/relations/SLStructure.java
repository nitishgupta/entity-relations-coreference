package edu.illinois.cs.cogcomp.erc.sl.relations;

import edu.illinois.cs.cogcomp.sl.core.IStructure;

/**
 * @author Bhargav Mangipudi
 */
public class SLStructure implements IStructure {
    private String relationLabel;

    public SLStructure(String relationLabel) {
        this.relationLabel = relationLabel;
    }

    public String getRelationLabel() {
        return this.relationLabel;
    }
}
