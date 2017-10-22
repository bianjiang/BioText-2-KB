package edu.ufl.biotext2kb.extraction.preprocessing;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import edu.ufl.biotext2kb.GuiceJUnit4Runner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(GuiceJUnit4Runner.class)
public class PreprocessingTest {

    private static final Logger LOG = LoggerFactory.getLogger(PreprocessingTest.class);

    private static String mockAbstractText = "Obesity and type 2 diabetes are becoming increasingly prevalent worldwide, and both are associated with an increased incidence and mortality from many cancers." +
            " The metabolic abnormalities associated with type 2 diabetes develop many years before the onset of diabetes and, therefore, may be contributing to cancer risk before individuals are aware that they are at risk." +
            " Multiple factors potentially contribute to the progression of cancer in obesity and type 2 diabetes, including hyperinsulinemia and insulin-like growth factor I, hyperglycemia, dyslipidemia, adipokines and cytokines, and the gut microbiome." +
            " These metabolic changes may contribute directly or indirectly to cancer progression." +
            " Intentional weight loss may protect against cancer development, and therapies for diabetes may prove to be effective adjuvant agents in reducing cancer progression." +
            " In this review we discuss the current epidemiology, basic science, and clinical data that link obesity, diabetes, and cancer and how treating obesity and type 2 diabetes could also reduce cancer risk and improve outcomes.\n";


    @Inject
    private Preprocessing preprocessing;

    @Test
    public void testSentenceSegmentation() {

        ImmutableSet<String> sentences = preprocessing.sentenceSegmentation(mockAbstractText);

        for(String sentence:sentences){
            LOG.info(sentence);
        }
    }

}
