package edu.illinois.cs.cogcomp.erc.reader;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.erc.features.pipeline;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.util.Utils;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.AceFileProcessor;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.ReadACEAnnotation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 2/25/16.
 */
public abstract class DocumentReader {
    protected String baseDir;
    protected String filelist_path;
    protected String serializedDoc_Dir;
    protected boolean is2004;

    protected static AceFileProcessor fileProcessor = new AceFileProcessor(new CcgTextAnnotationBuilder(new IllinoisTokenizer()));

    public Document readDocument(String fileName) {
        ReadACEAnnotation.is2004mode = is2004;

        String prefix = Utils.getCorpusTypeFromFilename(fileName);
        String fullFileName = this.baseDir + fileName;

        System.out.println(fullFileName);

        if (!new File(fullFileName).exists())
            System.out.println("Document File not found!");

        ACEDocument doc = fileProcessor.processAceEntry(new File(this.baseDir + prefix + "/"), fullFileName);
        TextAnnotation ta = fileProcessor.populateTextAnnotation(doc).get(0);

        return new Document(ta, doc.aceAnnotation, is2004, fileName);
    }

    public void readCorpus_and_WriteSerialized(){
        ReadACEAnnotation.is2004mode = is2004;

        System.out.println(serializedDoc_Dir);

        //List<Document> docs = new ArrayList<Document>();
        List<String> lines = null;
        try{
            lines = LineIO.read(filelist_path);
        } catch(IOException e) {
            e.printStackTrace();
        }

        for(String filename : lines){
            Document doc = null;
            try{
                doc = readDocument(filename);
                Utils.writeSerializedDocument(doc, serializedDoc_Dir, filename);
            } catch (Exception e){
                System.out.println(filename);
                e.printStackTrace();
            }
        }

    }
}
