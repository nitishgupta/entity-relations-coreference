package edu.illinois.cs.cogcomp.erc.main;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusUtils;
import edu.illinois.cs.cogcomp.erc.ir.DocUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.reader.Ace05Reader;
import edu.illinois.cs.cogcomp.erc.util.Utils;

import javax.xml.soap.Text;
import java.io.File;
import java.util.List;

/**
 * Created by nitishgupta on 2/19/16.
 */
public class Main {

    public static void main(String [] args) {
        ConfigSystem.initialize();

        List<Corpus> corpora  = CorpusUtils.readACE05CompleteTrainDevTestCorpora();
        Corpus ace05 = corpora.get(0);
        Corpus ace05train = corpora.get(1);
        Corpus ace05dev = corpora.get(2);
        Corpus ace05test = corpora.get(3);




    }
}
