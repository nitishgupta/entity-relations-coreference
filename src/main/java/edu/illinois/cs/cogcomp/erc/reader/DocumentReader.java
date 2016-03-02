package edu.illinois.cs.cogcomp.erc.reader;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.util.Utils;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.AceFileProcessor;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.ReadACEAnnotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 2/25/16.
 */
public abstract class DocumentReader {
    protected String baseDir;
    protected String filelistPath;
    protected String serializedDocDir;
    protected boolean is2004;



    protected static AceFileProcessor fileProcessor = new AceFileProcessor(new CcgTextAnnotationBuilder(new IllinoisTokenizer()));

    public Document readDocument(String fileName) {
        String cacheFileName = Utils.getCacheFilenameForDocument(fileName);

        // If cached file exists, try to read it first!
        if (new File(serializedDocDir + cacheFileName).exists()) {
            Document cachedDocument = Utils.readSerializedDocument(serializedDocDir, cacheFileName);
            if (cachedDocument != null) return cachedDocument;
        }

        // Read document from dataset
        ReadACEAnnotation.is2004mode = this.is2004;

        String prefix = Utils.getCorpusTypeFromFilename(fileName);
        String fullFileName = this.baseDir + fileName;

        if (!new File(fullFileName).exists())
            System.out.println("Document File not found! - " + fullFileName);

        try {
            ACEDocument doc = fileProcessor.processAceEntry(new File(this.baseDir + prefix + "/"), fullFileName);
            TextAnnotation ta = AceFileProcessor.populateTextAnnotation(doc).get(0);
            Document newDoc = new Document(ta, doc.aceAnnotation, is2004, fileName);

            // Write document to cache
            Utils.writeSerializedDocument(newDoc, serializedDocDir, cacheFileName);

            return newDoc;
        } catch (Exception ex) {
            if (Parameters.isDebug) System.err.println("Failed to parse document - " + fullFileName);
            if (Parameters.isDebug) ex.printStackTrace(System.err);
        }

        return null;
    }

    public List<Document> readCorpus() {
        try {
            List<Document> documents = new ArrayList<>();
            for (String file : LineIO.read(this.filelistPath)) {
                Document doc = readDocument(file);

                if (doc != null) documents.add(doc);
            }

            return documents;
        } catch (FileNotFoundException ex) {
            if (Parameters.isDebug) System.err.println("Cannot find file list! at " + this.filelistPath);
        }

        return null;
    }
}
