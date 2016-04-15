package edu.illinois.cs.cogcomp.erc.sl.relations;

import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandIgnore;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusType;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public class MainClass {

    public static Logger logger = LoggerFactory.getLogger(MainClass.class);

    @CommandDescription(usage = "", description = "Read Corpus Only")
    public static Corpus readCorpus(String corpusType) {
        Corpus aceCorpus = null;

        try {
            CorpusType type = CorpusUtils.getCorpusTypeEnum(corpusType);
            aceCorpus = CorpusUtils.readCorpus(type);
        } catch (Exception ex) {
            logger.error("Error while parsing corpus type", ex);
        }

        return aceCorpus;
    }

    @CommandDescription(usage = "", description = "")
    public static void train(String corpusType) throws IOException, ClassNotFoundException { train(corpusType, null); }

    @CommandDescription(usage = "", description = "")
    public static void train(String corpusType, String modelFileName) throws IOException, ClassNotFoundException {
        Corpus aceCorpus = MainClass.readCorpus(corpusType);

        List<Corpus> allCorpora = CorpusUtils.getTrainDevTestCorpora(aceCorpus);
        Corpus trainCorpus = allCorpora.get(0);
        Corpus develCorpus = allCorpora.get(1);

        SLModel slModel = SLModel.loadModel(modelFileName);

        for (Document doc : aceCorpus.getDocs()) {
            SLHelper.populateSLProblemForDocument(doc, "", slModel.lm);
        }
    }

    @CommandDescription(usage = "", description = "")
    public static void test(String corpusType, String modelFileName) throws Exception {
        SLModel slModel = SLModel.loadModel(modelFileName);
        testModel(corpusType, slModel);
    }

    @CommandDescription(usage = "", description = "")
    public static void trainAndTest(String corpusType) throws Exception { trainAndTest(corpusType, null); }

    @CommandDescription(usage = "", description = "")
    public static void trainAndTest(String corpusType, String modelFileName) throws Exception {
        train(corpusType);
        test(corpusType, modelFileName);
    }

    @CommandIgnore
    public static void testModel(String corpusType, SLModel modelInstance) {
        Corpus aceCorpus = MainClass.readCorpus(corpusType);

        List<Corpus> allCorpora = CorpusUtils.getTrainDevTestCorpora(aceCorpus);
        Corpus testCorpus = allCorpora.get(2);
    }

    @CommandIgnore
    public static void main(String[] args) throws Exception {
        ConfigSystem.initialize();
        InteractiveShell<MainClass> shell = new InteractiveShell<>(MainClass.class);

        if (args.length == 0) {
            shell.showDocumentation();
        } else {
            shell.runCommand(args);
        }
    }
}
