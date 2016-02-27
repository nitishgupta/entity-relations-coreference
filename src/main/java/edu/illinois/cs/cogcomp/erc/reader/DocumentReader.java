package edu.illinois.cs.cogcomp.erc.reader;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.erc.features.Pos;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.util.Utils;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.AceFileProcessor;

import java.io.File;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 2/25/16.
 */
public abstract class DocumentReader {
    protected String baseDir;
    protected static AceFileProcessor fileProcessor = new AceFileProcessor(new CcgTextAnnotationBuilder(new IllinoisTokenizer()));

    public void testProcessDocument(String TEST_DIR, String TEST_FILE)
    {
        File file = new File(TEST_DIR + TEST_FILE);
        if(!file.exists())
            System.out.println("Document File not found!");

        ACEDocument doc = fileProcessor.processAceEntry(new File(TEST_DIR), TEST_DIR + TEST_FILE);

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

    public Document readDocument(String fileName) {
        String prefix = Utils.getCorpusTypeFromFilename(fileName);
        String fullFileName = this.baseDir + fileName;

        if (!new File(fullFileName).exists())
            System.out.println("Document File not found!");

        ACEDocument doc = fileProcessor.processAceEntry(new File(this.baseDir + prefix + "/"), fileName);
        TextAnnotation ta = fileProcessor.populateTextAnnotation(doc).get(0);

        return new Document(ta, doc.aceAnnotation);
    }
}
