package edu.illinois.cs.cogcomp.features;

import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
import edu.illinois.cs.cogcomp.annotation.AnnotatorServiceConfigurator;
import edu.illinois.cs.cogcomp.config.PipelineConfig;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator;
import edu.illinois.cs.cogcomp.nlp.pipeline.IllinoisPipelineFactory;
import edu.illinois.cs.cogcomp.nlp.util.SimpleCachingPipeline;

/**
 * Created by nitishgupta on 2/21/16.
 */
public class Pos {

    private static AnnotatorService prep;

    public static void addShallowParse(TextAnnotation ta) {
        ResourceManager pipelineRm = new PipelineConfig().getDefaultConfig();
        ResourceManager annotatorServiceRm = new AnnotatorServiceConfigurator().getConfig(pipelineRm);

        try {
            prep = IllinoisPipelineFactory.buildPipeline(annotatorServiceRm);
        } catch (Exception e){
            e.printStackTrace();
        }

        try{
            prep.addView(ta, ViewNames.SHALLOW_PARSE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void addPOS(TextAnnotation ta) {
        ResourceManager pipelineRm = new PipelineConfig().getDefaultConfig();
        ResourceManager annotatorServiceRm = new AnnotatorServiceConfigurator().getConfig(pipelineRm);

        try {
            prep = IllinoisPipelineFactory.buildPipeline(annotatorServiceRm);
        } catch (Exception e){
            e.printStackTrace();
        }

        try{
            prep.addView(ta, ViewNames.POS);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
