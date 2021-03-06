package edu.illinois.cs.cogcomp.erc.util;

import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
import edu.illinois.cs.cogcomp.annotation.AnnotatorServiceConfigurator;
import edu.illinois.cs.cogcomp.erc.config.PipelineConfig;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory;

/**
 * Created by nitishgupta on 2/21/16.
 */
public class PipelineService {

    private static AnnotatorService prep;

    private static AnnotatorService getAnnotatorServiceInstance() {
        if (prep == null) {
            ResourceManager pipelineRm = new PipelineConfig().getDefaultConfig();
            ResourceManager annotatorServiceRm = new AnnotatorServiceConfigurator().getConfig(pipelineRm);

            try {
                prep = IllinoisPipelineFactory.buildPipeline(annotatorServiceRm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return prep;
    }

    public static void addShallowParse(TextAnnotation ta) {
        if (ta == null || ta.hasView(ViewNames.SHALLOW_PARSE)) {
            return;
        }

        AnnotatorService prep = getAnnotatorServiceInstance();

        try {
            prep.addView(ta, ViewNames.SHALLOW_PARSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addPOS(TextAnnotation ta) {
        if (ta == null || ta.hasView(ViewNames.POS)) {
            return;
        }

        AnnotatorService prep = getAnnotatorServiceInstance();

        try {
            prep.addView(ta, ViewNames.POS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Add all required views that we use in the experiments
    public static void addRequiredViews(TextAnnotation ta) {
        addPOS(ta);
        addShallowParse(ta);
    }
}
