package edu.illinois.cs.cogcomp.erc.sl.coref;

import edu.illinois.cs.cogcomp.sl.core.IStructure;

/**
 * Created by nitishgupta on 4/17/16.
 */
public class CorefLabel implements IStructure {
    String link;


    public CorefLabel(String link){
        this.link = link;
    }

    public String getCorefLink(){  return link;  }

    @Override
    public String toString() {  return "";  }

}
