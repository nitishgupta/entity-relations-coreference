package edu.illinois.cs.cogcomp.ace04;


import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.AceFileProcessor;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.ReadACEAnnotation;

import java.io.File;
import java.util.List;

/**
 * Created by nitishgupta on 2/19/16.
 */
public class reader {
    private static final String TEST_DIR="data/ace04/data/English/nw/";
    private static final String TEST_FILE="APW20001211.1441.0436.apf.xml";

    public static void testProcessDocument()
    {
        ReadACEAnnotation.is2004mode = true;
        AceFileProcessor proc = new AceFileProcessor(new CcgTextAnnotationBuilder(new IllinoisTokenizer()));

        File file = new File(TEST_DIR + TEST_FILE);
        if(file.exists())
            System.out.println("Hurrya");

        ACEDocument doc = proc.processAceEntry(new File(TEST_DIR), TEST_DIR + TEST_FILE);

        List<TextAnnotation> taList = AceFileProcessor.populateTextAnnotation(doc);

        for ( TextAnnotation ta : taList )
        {
            System.out.println(ta.getAvailableViews());
        }
    }
}
