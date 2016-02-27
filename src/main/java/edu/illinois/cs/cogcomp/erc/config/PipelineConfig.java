package edu.illinois.cs.cogcomp.erc.config;

import edu.illinois.cs.cogcomp.annotation.AnnotatorServiceConfigurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Property;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;

/**
 * Created by nitishgupta on 2/21/16.
 */
public class PipelineConfig extends Configurator{

    public static final Property STFRD_TIME_PER_SENTENCE = new Property("stanfordMaxTimePerSentence", "1000");
    public static final Property STFRD_MAX_SENTENCE_LENGTH = new Property("stanfordParseMaxSentenceLength", "80");
    public static final Property SIMPLE_CACHE_DIR = new Property("simpleCacheDir", "simple-annotation-cache");
    public static final Property USE_POS;
    public static final Property USE_LEMMA;
    public static final Property USE_SHALLOW_PARSE;
    public static final Property USE_NER_CONLL;
    public static final Property USE_NER_ONTONOTES;
    public static final Property USE_STANFORD_PARSE;
    public static final Property USE_STANFORD_DEP;
    public static final Property USE_SRL_VERB;
    public static final Property USE_SRL_NOM;
    public static final Property THROW_EXCEPTION_ON_FAILED_LENGTH_CHECK;

    public PipelineConfig() {
    }

    public ResourceManager getDefaultConfig() {
        Property[] properties = new Property[]{STFRD_TIME_PER_SENTENCE, STFRD_MAX_SENTENCE_LENGTH, SIMPLE_CACHE_DIR, USE_POS, USE_LEMMA, USE_SHALLOW_PARSE, USE_NER_CONLL, USE_NER_ONTONOTES, USE_STANFORD_PARSE, USE_STANFORD_DEP, USE_SRL_VERB, USE_SRL_NOM, THROW_EXCEPTION_ON_FAILED_LENGTH_CHECK};
        return (new AnnotatorServiceConfigurator()).getConfig(new ResourceManager(this.generateProperties(properties)));
    }

    static {
        USE_POS = new Property("usePos", TRUE);
        USE_LEMMA = new Property("useLemma", FALSE);
        USE_SHALLOW_PARSE = new Property("useShallowParse", TRUE);
        USE_NER_CONLL = new Property("useNerConll", FALSE);
        USE_NER_ONTONOTES = new Property("useNerOntonotes", FALSE);
        USE_STANFORD_PARSE = new Property("useStanfordParse", FALSE);
        USE_STANFORD_DEP = new Property("useStanfordDep", FALSE);
        USE_SRL_VERB = new Property("useSrlVerb", FALSE);
        USE_SRL_NOM = new Property("useSrlNom", FALSE);
        THROW_EXCEPTION_ON_FAILED_LENGTH_CHECK = new Property("throwExceptionOnFailedLengthCheck", TRUE);
    }
}
