package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.experiments.ClassificationTester;
import edu.illinois.cs.cogcomp.core.experiments.evaluators.ConstituentLabelingEvaluator;
import edu.illinois.cs.cogcomp.core.experiments.evaluators.Evaluator;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandIgnore;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusType;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;

import edu.illinois.cs.cogcomp.erc.sl.ner.annotators.BIOAnnotator;
import edu.illinois.cs.cogcomp.erc.sl.ner.annotators.NERAnnotator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 3/9/16.
 */
public class MainClass {
    public static final String DefaultNERModel = "DefaultNERModel.model";

    /**
     * This SLProblem returned by this method has List<Constituents> as IInstance.
     * For test data, the IInstance doesn't encode that a given token is unknown.
     * While creating lexical features, a check for each Constituent token needs to be put for whether the word exists in the Lexiconer.
     *
     * @param corpus : The corpus for which the <IInstance, IStructure> pairs need to be retured.
     * @param lm : To store the sspace of labels - BIO labels in this case
     * @return  Instance of the SLProblem
     */
    @CommandIgnore
    public static SLProblem readStructuredData(Corpus corpus, Lexiconer lm, String viewName) {
        SLProblem sp = new SLProblem();
        int num_instances = 0;

        // In this loop, the number of instances added to the SLProblem = SentenceView.getConstituents().size()*corpus.numDocs()
        List<Document> docs = corpus.getDocs();
        for(Document doc : docs){
            TokenLabelView NER_GOLD_BIO_VIEW = null;
            if(viewName.equals(Corpus.NER_GOLD_EXTENT_BIO_VIEW))
                NER_GOLD_BIO_VIEW = doc.getNERExtentBIOView();
            else if(viewName.equals(Corpus.NER_GOLD_HEAD_BIO_VIEW))
                NER_GOLD_BIO_VIEW = doc.getNERHeadBIOView();
            else{
                System.out.println("View Name not found : " + viewName);
                System.exit(0);
            }

            View POS_VIEW = doc.getTA().getView(ViewNames.POS);
            View SentenceView = doc.getSentenceView();

            // One Sentence inside. Therefore one SequenceInstance and SequenceLabel should be made
            // In this loop, the number of instances added to the SLProblem = SentenceView.getConstituents().size()
            for(Constituent sentence : SentenceView.getConstituents()){
                SequenceInstance sen = null;
                SequenceLabel sen_label = null;

                int start_token = sentence.getStartSpan();  // Inclusive
                int end_token = sentence.getEndSpan() - 1;  // Inclusive

                List<Constituent> token_constituents = new ArrayList<Constituent>();
                List<String> posTag_list = new ArrayList<>();
                String[] labels = new String[end_token - start_token + 1];;
                //int[] tagIds = new int[end_token - start_token + 1];

                for(int token_num = start_token; token_num <= end_token; token_num++){
                    Constituent c = NER_GOLD_BIO_VIEW.getConstituentAtToken(token_num);
                    token_constituents.add(c);

                    String posTag = POS_VIEW.getConstituentsCoveringToken(token_num).get(0).getLabel();
                    posTag_list.add(posTag);

//                    if (lm.isAllowNewFeatures()) {
//                        lm.addFeature(LexiconerConstants.WORD_PREFIX + c.getSurfaceForm());
//                        lm.addFeature(LexiconerConstants.POS_PREFIX + posTag);
//                    }

                    labels[token_num - start_token] = c.getLabel();
                    if (lm.isAllowNewFeatures()) {
                        lm.addLabel(c.getLabel());
                    }
                    //String labelTag = LexiconerConstants.LABEL_PREFIX + c.getLabel();
//                    if (lm.isAllowNewFeatures()) {
//                        lm.addLabel(labelTag);
//                    }

                    //assert lm.containsLabel(labelTag) : "Error: Previously unseen label found during testing.";
                    //tagIds[token_num - start_token] = lm.getLabelId(labelTag);
                }

                sen = new SequenceInstance(token_constituents, posTag_list);
                sen_label = new SequenceLabel(labels);
                sp.addExample(sen, sen_label);
                num_instances++;
            }
        }

        System.out.println("Num of Instances added in the SLProblem = " + num_instances);
        return sp;
    }

    @CommandIgnore
    public static IInstance getIInstanceForSentence(Constituent sentence, Lexiconer lm){
        TextAnnotation ta = sentence.getTextAnnotation();
        TokenLabelView TokensView = (TokenLabelView)ta.getView(Corpus.TOKENS_VIEW);
        View POSView = ta.getView(ViewNames.POS);

        int start_token = sentence.getStartSpan();  // Inclusive
        int end_token = sentence.getEndSpan() - 1;  // Inclusive


        List<Constituent> token_constituents = new ArrayList<Constituent>();
        List<String> posTag_list = new ArrayList<>();

        for(int token_num = start_token; token_num <= end_token; token_num++){
            Constituent c = TokensView.getConstituentAtToken(token_num);
            token_constituents.add(c);

            String posTag = POSView.getConstituentsCoveringToken(token_num).get(0).getLabel();
            posTag_list.add(posTag);
        }

        IInstance sen = new SequenceInstance(token_constituents, posTag_list);

        return sen;
    }

    @CommandDescription(description = "Train the NER Model.")
    public static void train() throws Exception {
        train(DefaultNERModel);
    }

    @CommandDescription(description = "Train the NER Model. Parameter : ModelName")
    public static void train(String modelFileName) throws Exception {
        List<Corpus> corpora  = CorpusUtils.readCompleteTrainDevTestCorpora(CorpusType.ACE05);
        Corpus trainData = corpora.get(1);

        String goldViewName = Corpus.NER_GOLD_HEAD_BIO_VIEW;

        Train.trainNER(trainData, Parameters.SL_PARAMETER_CONFIG_FILE, modelFileName, goldViewName);
    }

    @CommandDescription(description = "Test the NER Model.")
    public static void test() throws Exception {
        test(DefaultNERModel);
    }

    @CommandDescription(description = "Test the NER Model.")
    public static void test(String modelFileName) throws Exception {
        CorpusType corpusType = CorpusType.ACE05;
        String predictedBIOView = Corpus.NER_PRED_HEAD_BIO_VIEW;
        String predictedEntityView = "ENTITYVIEW";

        SLModel model = SLModel.loadModel(modelFileName);

        List<Corpus> corpora  = CorpusUtils.readCompleteTrainDevTestCorpora(corpusType);
        Corpus testData = corpora.get(3);

        Annotator annotator = new NERAnnotator(model, corpusType == CorpusType.ACE04, predictedBIOView, predictedEntityView);
        Evaluator evaluator = new ConstituentLabelingEvaluator();
        ClassificationTester tester = new ClassificationTester();

        String goldViewName;
        String predictedViewName;

        if (annotator instanceof NERAnnotator) {
            goldViewName = ViewNames.NER_ACE_COARSE_HEAD;
            predictedViewName = predictedEntityView;
        } else if (annotator instanceof BIOAnnotator) {
            goldViewName = Corpus.NER_GOLD_HEAD_BIO_VIEW;
            predictedViewName = predictedBIOView;
            tester.ignoreLabelFromSummary("O");
        } else {
            System.out.println("Invalid annotator configuration.");
            return;
        }

        for (Document doc : testData.getDocs()) {
            TextAnnotation ta = doc.getTA();
            annotator.addView(ta);

            // Evaluator does an .equals on constituents and expects same view name
            SpanLabelView goldViewWithPredictedName = new SpanLabelView(predictedViewName, ta);
            for (Constituent c : ta.getView(goldViewName)) {
                Constituent cloneCons = new Constituent(
                        c.getLabel(),
                        predictedViewName,
                        c.getTextAnnotation(),
                        c.getStartSpan(),
                        c.getEndSpan());
                goldViewWithPredictedName.addConstituent(cloneCons);
            }

            evaluator.evaluate(tester, goldViewWithPredictedName, ta.getView(predictedViewName));
        }

        Test.writeNEROutput(testData, predictedEntityView, "pred_ner_newloss.txt");

        System.out.println(tester.getPerformanceTable().toOrgTable());
    }

    /**
     * Main method.
     * @param args List of command-line argument.
     */
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
