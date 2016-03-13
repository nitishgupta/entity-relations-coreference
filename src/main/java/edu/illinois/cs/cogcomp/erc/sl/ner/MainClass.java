package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;

import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 3/9/16.
 */
public class MainClass {
    /**
     * This SLProblem returned by this method has List<Constituents> as IInstance.
     * For test data, the IInstance doesn't encode that a given token is unknown.
     * While creating lexical features, a check for each Constituent token needs to be put for whether the word exists in the Lexiconer.
     *
     * @param corpus : The corpus for which the <IInstance, IStructure> pairs need to be retured.
     * @param lm : Lexiconer that has the word and label space lexicon
     * @return  Instance of the SLProblem
     */
    public static SLProblem readStructuredData(Corpus corpus, Lexiconer lm) {
        SLProblem sp = new SLProblem();
        int num_instances = 0;
        if(lm.isAllowNewFeatures())
            lm.addFeature(LexiconerConstants.UNKNOWN_WORD);

        // In this loop, the number of instances added to the SLProblem = SentenceView.getConstituents().size()*corpus.numDocs()
        List<Document> docs = corpus.getDocs();
        for(Document doc : docs){
            TokenLabelView NER_GOLD_BIO_VIEW = doc.getNERBIOView();
            View SentenceView = doc.getSentenceView();

            // One Sentence inside. Therefore one SequenceInstance and SequenceLabel should be made
            // In this loop, the number of instances added to the SLProblem = SentenceView.getConstituents().size()
            for(Constituent sentence : SentenceView.getConstituents()){
                SequenceInstance sen = null;
                SequenceLabel sen_label = null;

                int start_token = sentence.getStartSpan();  // Inclusive
                int end_token = sentence.getEndSpan() - 1;  // Inclusive

                List<Constituent> token_constituents = new ArrayList<Constituent>();
                int[] tagIds = new int[end_token - start_token + 1];

                for(int token_num = start_token; token_num <= end_token; token_num++){
                    Constituent c = NER_GOLD_BIO_VIEW.getConstituentAtToken(token_num);
                    token_constituents.add(c);

                    if (lm.isAllowNewFeatures()) {
                        lm.addFeature(LexiconerConstants.WORD_PREFIX + c.getSurfaceForm());
                    }

                    String labelTag = LexiconerConstants.LABEL_PREFIX + c.getLabel();
                    if (lm.isAllowNewFeatures()) {
                        lm.addLabel(labelTag);
                    }

                    assert lm.containsLabel(labelTag) : "Error: Previously unseen label found during testing.";
                    tagIds[token_num - start_token] = lm.getLabelId(labelTag);
                }

                sen = new SequenceInstance(token_constituents);
                sen_label = new SequenceLabel(tagIds);
                sp.addExample(sen, sen_label);
                num_instances++;
            }
        }

        System.out.println("Num of Instances added in the SLProblem = " + num_instances);
        return sp;
    }

    /**
     * Main method.
     * @param args List of command-line argument.
     */
    public static void main(String[] args) {
        ConfigSystem.initialize();

        try {
            List<Corpus> corpora  = CorpusUtils.readACE05CompleteTrainDevTestCorpora();
            Corpus allCorpora = corpora.get(0);
            Corpus trainData = corpora.get(1);
            Corpus devData = corpora.get(2);
            Corpus testData = corpora.get(3);

            Train.trainNER(trainData, Parameters.SL_PARAMETER_CONFIG_FILE, "testModel");
            Test.testNER(testData, "testModel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
