package edu.illinois.cs.cogcomp.erc.reader;

import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.ReadACEAnnotation;

/**
 * Created by nitishgupta on 2/25/16.
 */
public class Ace05Reader extends DocumentReader {

    public Ace05Reader(){
        super();
        this.is2004 = false;
        this.baseDir = Parameters.ACE05_DATA_DIR;
        this.filelist_path = Parameters.ACE05_FILELIST;
        this.serializedDoc_Dir = Parameters.ACE05_SERIALIZED_DOCS;
    }

//    @Override
//    public Document readDocument(String filename, boolean is2004){
//        ReadACEAnnotation.is2004mode = false;
//        return super.readDocument(filename, this.is2004);
//    }
}
