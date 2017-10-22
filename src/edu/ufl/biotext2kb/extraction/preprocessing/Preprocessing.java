package edu.ufl.biotext2kb.extraction.preprocessing;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Preprocessing extends AbstractModule {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Preprocessing.class);


    private StanfordCoreNLP coreNLPPipeline = new StanfordCoreNLP(
            PropertiesUtils.asProperties(
                    "annotators", "tokenize,ssplit",
                    "tokenize.language", "en"
            )
    );

    public ImmutableSet<String> sentenceSegmentation(String docText) {

        Annotation document = new Annotation(docText);

        coreNLPPipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        ImmutableSet.Builder<String> sentencesTextBuilder = ImmutableSet.<String>builder();

        for (CoreMap sentence: sentences) {
            String sentenceText = sentence.get(CoreAnnotations.TextAnnotation.class).toLowerCase();
            //LOG.info(sentenceText);
            sentencesTextBuilder.add(sentenceText);

        }

        return sentencesTextBuilder.build();
    }

    @Override
    protected void configure(){

    }

}
