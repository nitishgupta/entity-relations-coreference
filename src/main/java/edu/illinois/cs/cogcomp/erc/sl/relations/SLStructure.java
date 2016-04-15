package edu.illinois.cs.cogcomp.erc.sl.relations;

import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.QueryableList;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.sl.core.IStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public class SLStructure implements IStructure {
    private List<Relation> relations;

    public SLStructure(List<Relation> relationList) {
        this.relations = new ArrayList<>(relationList);
    }

    public List<Relation> getRelations() {
        return this.relations;
    }
}
