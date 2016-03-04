package edu.illinois.cs.cogcomp.erc.reader;

import edu.illinois.cs.cogcomp.erc.config.Parameters;

/**
 * Created by nitishgupta on 2/25/16.
 */
public class Ace05Reader extends DocumentReader {

    public Ace05Reader() {
        super();

        this.is2004 = false;
        this.baseDir = Parameters.ACE05_DATA_DIR;
        this.filelistPath = Parameters.ACE05_FILELIST;
        this.serializedDocDir = Parameters.ACE05_SERIALIZED_DOCS;
    }

    public boolean checkis2004(){
        return this.is2004;
    }
}
