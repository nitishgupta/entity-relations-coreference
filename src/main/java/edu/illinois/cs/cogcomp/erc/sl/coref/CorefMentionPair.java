package edu.illinois.cs.cogcomp.erc.sl.coref;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.wikifier.utils.io.Console;

/**
 * Created by nitishgupta on 4/17/16.
 */
public class CorefMentionPair implements IInstance {
    Constituent c1, c2;

    final int hashCode;

    /**
     * Stores mention with lower start span (appearing first in text) as c1 and other as c2
     * @param c1 One of the consitutents of the pair
     * @param c2 One of the consitutents of the pair
     */
    public CorefMentionPair(Constituent c1, Constituent c2){
        assert c1.getTextAnnotation() == c2.getTextAnnotation();
        int startc1 = c1.getStartSpan(), startc2 = c2.getStartSpan();

        if(startc1 < startc2){
            this.c1 = c1;
            this.c2 = c2;
        } else if(startc1 > startc2){
            this.c1 = c2;
            this.c2 = c1;
        } else {
            System.err.println("Same mention is trivially co-referent");
            System.exit(0);
        }

        int hashCodeVal = 0;
        hashCodeVal += this.c1.hashCode()*19; // Verify
        hashCodeVal += this.c2.hashCode()*19; // Verify
        this.hashCode = hashCodeVal;
    }

    public Constituent getFirstConstituent(){  return c1;   }

    public Constituent getSecondConstituent(){  return c2;   }

    public Pair<Constituent, Constituent> getConstituentsAsPair(){
        Pair<Constituent, Constituent> constituentPair = new Pair<Constituent, Constituent>(this.c1, this.c2);
        return constituentPair;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
