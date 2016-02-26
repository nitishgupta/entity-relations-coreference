package edu.illinois.cs.cogcomp.reader;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.features.Pos;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.AceFileProcessor;

import java.io.File;
import java.util.List;

/**
 * Created by nitishgupta on 2/25/16.
 */
public class Ace05Reader {
    public static final String TEST_DIR="data/ace05/data/English/nw/";
    public static final String TEST_FILE="AFP_ENG_20030625.0057.apf.xml";

    public static void testProcessDocument(String TEST_DIR, String TEST_FILE)
    {
        AceFileProcessor proc = new AceFileProcessor(new CcgTextAnnotationBuilder(new IllinoisTokenizer()));

        File file = new File(TEST_DIR + TEST_FILE);
        if(file.exists())
            System.out.println("Hurrya");

        ACEDocument doc = proc.processAceEntry(new File(TEST_DIR), TEST_DIR + TEST_FILE);

        List<TextAnnotation> taList = AceFileProcessor.populateTextAnnotation(doc);

        System.out.println(taList.size());

        TextAnnotation ta = taList.get(0);

        System.out.println(ta.getAvailableViews());




        Pos.addPOS(ta);
        Pos.addShallowParse(ta);
        System.out.println(ta.getAvailableViews());

        View view = ta.getView("SHALLOW_PARSE");

        for(Constituent c : view.getConstituents()){
            System.out.print(c.getSurfaceForm() + " [" + c.getLabel() + " " + c.getStartSpan() + " " + c.getEndSpan()
                            + " ] "
            );
        }



    }
}
