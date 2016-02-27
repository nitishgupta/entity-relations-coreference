package edu.illinois.cs.cogcomp.erc.reader;


import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.ReadACEAnnotation;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by nitishgupta on 2/25/16.
 */
public class Ace04Reader extends DocumentReader {

    public Ace04Reader(){
        super();
        this.is2004 = true;
        this.baseDir = Parameters.ACE04_DATA_DIR;
        this.filelist_path = Parameters.ACE04_FILELIST;
        this.serializedDoc_Dir = Parameters.ACE04_SERIALIZED_DOCS;
    }

//    @Override
//    public Document readDocument(String filename, boolean is2004){
//        ReadACEAnnotation.is2004mode = true;
//        return super.readDocument(filename, this.is2004);
//    }

}
