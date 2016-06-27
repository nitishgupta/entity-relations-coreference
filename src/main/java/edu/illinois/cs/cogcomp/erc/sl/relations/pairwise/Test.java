package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by nitishgupta on 5/12/16.
 */
public class Test {

    public static void writeRelOutput(Corpus corpus, String nerViewName, String outputfilepath){
        StringBuilder output = new StringBuilder();

        for(Document d : corpus.getDocs()){
            TextAnnotation ta = d.getTA();
            String id = ta.getId();
            PredicateArgumentView view = (PredicateArgumentView ) ta.getView(nerViewName);
            List<Constituent> constituents = view.getPredicates();
            for(Constituent c : constituents) {
                Constituent c1 = c.getOutgoingRelations().get(0).getTarget();
                String rel = c.getOutgoingRelations().get(0).getRelationName();
                String out = id + "\t";   // DOCID
                int start = c.getStartSpan() + c1.getStartSpan();
                int end = c.getEndSpan() + c1.getEndSpan();
                out += start + "\t";         // SPAN START
                out += end + "\t";         // SPAN END
                out += "NIL" + id + "-" + "\t";               // CLUSTER ID
                out += "1.0" + "\t";                            // SCORE
                out += rel + "\n";                     // NER TYPE
                output.append(out);
            }
        }

        String output_string = output.toString();
        try {
            FileUtils.writeStringToFile(new File(outputfilepath), output_string);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Solution Written to File - Succesfully");
    }
}
