package edu.illinois.cs.cogcomp.erc.sl.coref;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by nitishgupta on 5/11/16.
 */
public class CorefEvaluator {

    public static void writeChainSolutionToFile(Corpus corpus, String corefViewName, String outputfilepath) {
        StringBuilder output = new StringBuilder();

        for(Document d : corpus.getDocs()){
            TextAnnotation ta = d.getTA();
            String id = ta.getId();
            CoreferenceView view = (CoreferenceView) ta.getView(corefViewName);
            List<Constituent> constituents = view.getConstituents();
            for(Constituent c : constituents) {
                String out = id + "\t";   // DOCID
                int start = c.getStartSpan();
                int end = c.getEndSpan();
                out += start + "\t";         // SPAN START
                out += end + "\t";         // SPAN END
                out += "NIL" + id + "-" + c.getLabel()+ "\t";               // CLUSTER ID
                out += "1.0" + "\t";                            // SCORE
                out += "RAN" + "\n";                     // NER TYPE
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


    public static void writeNERToFile(Corpus corpus, String nerViewName, String outputfilepath) {
        StringBuilder output = new StringBuilder();

        for(Document d : corpus.getDocs()){
            TextAnnotation ta = d.getTA();
            String id = ta.getId();
            SpanLabelView view = (SpanLabelView) ta.getView(nerViewName);
            List<Constituent> constituents = view.getConstituents();
            for(Constituent c : constituents) {
                String out = id + "\t";   // DOCID
                int start = c.getStartSpan();
                int end = c.getEndSpan();
                out += start + "\t";         // SPAN START
                out += end + "\t";         // SPAN END
                out += "NIL" + id + "-" + c.getLabel()+ "\t";               // CLUSTER ID
                out += "1.0" + "\t";                            // SCORE
                out += c.getLabel() + "\n";                     // NER TYPE
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
