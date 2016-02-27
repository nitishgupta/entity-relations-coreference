package edu.illinois.cs.cogcomp.erc.util;

import edu.illinois.cs.cogcomp.erc.ir.Document;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * Created by bhargav on 2/26/16.
 */
public class Utils {

    public static String getCorpusTypeFromFilename(String filename) {
        return filename.substring(0, filename.indexOf('/'));
    }

    public static void writeSerializedDocument(Document doc, String dir, String filename){
        filename = filename.replaceAll("/", "_");
        filename = filename.substring(0, filename.lastIndexOf(".")) + ".ser";

        File directory = new File(dir);
        try {
            FileUtils.forceMkdir(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String pathToWrite = dir + filename;
        System.out.println(pathToWrite);

        try (
                OutputStream file = new FileOutputStream(pathToWrite);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer);
        ){
            output.writeObject(doc);
            System.out.println("Serialized Document successfully written.");
        }
        catch(IOException ex){
            System.err.println("Cannot Write Corpus");
        }
    }
}
