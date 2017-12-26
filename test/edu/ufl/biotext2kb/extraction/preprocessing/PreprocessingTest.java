package edu.ufl.biotext2kb.extraction.preprocessing;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import edu.ufl.biotext2kb.GuiceJUnit4Runner;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@RunWith(GuiceJUnit4Runner.class)
public class PreprocessingTest {

    private static final Logger LOG = LoggerFactory.getLogger(PreprocessingTest.class);

    private static String mockAbstractText = "Obesity and type 2 diabetes are becoming increasingly prevalent worldwide, and both are associated with an increased incidence and mortality from many cancers." +
            " The metabolic abnormalities associated with type 2 diabetes develop many years before the onset of diabetes and, therefore, may be contributing to cancer risk before individuals are aware that they are at risk." +
            " Multiple factors potentially contribute to the progression of cancer in obesity and type 2 diabetes, including hyperinsulinemia and insulin-like growth factor I, hyperglycemia, dyslipidemia, adipokines and cytokines, and the gut microbiome." +
            " These metabolic changes may contribute directly or indirectly to cancer progression." +
            " Intentional weight loss may protect against cancer development, and therapies for diabetes may prove to be effective adjuvant agents in reducing cancer progression." +
            " In this review we discuss the current epidemiology, basic science, and clinical data that link obesity, diabetes, and cancer and how treating obesity and type 2 diabetes could also reduce cancer risk and improve outcomes.\n";

    private static ArrayList<String> mockAbstractTextSentences = new ArrayList<>(
            Arrays.asList("Obesity and type 2 diabetes are becoming increasingly prevalent worldwide, and both are associated with an increased incidence and mortality from many cancers.",
                    " The metabolic abnormalities associated with type 2 diabetes develop many years before the onset of diabetes and, therefore, may be contributing to cancer risk before individuals are aware that they are at risk.",
                    " Multiple factors potentially contribute to the progression of cancer in obesity and type 2 diabetes, including hyperinsulinemia and insulin-like growth factor I, hyperglycemia, dyslipidemia, adipokines and cytokines, and the gut microbiome.",
                    " These metabolic changes may contribute directly or indirectly to cancer progression.",
                    " Intentional weight loss may protect against cancer development, and therapies for diabetes may prove to be effective adjuvant agents in reducing cancer progression.",
                    " In this review we discuss the current epidemiology, basic science, and clinical data that link obesity, diabetes, and cancer and how treating obesity and type 2 diabetes could also reduce cancer risk and improve outcomes."
            )
    );

    private static StringBuilder testSampleText = new StringBuilder();

    @Inject
    private Preprocessing preprocessing;


    @BeforeClass
    public static void readInTestSample() throws IOException {

        Iterator<String> itr = FileUtils.readLines(new File("data/Sentencesfrom23Abstracts.txt"), "utf-8").iterator();

        for (; itr.hasNext(); ) {
            testSampleText.append(itr.next());
            testSampleText.append("\n");
        }
    }

    @Test
    public void testSentenceSegmentation() {

        ImmutableSet<String> sentences = preprocessing.sentenceSegmentation(mockAbstractText);

        for (String sentence : sentences) {
            LOG.info(sentence);
        }

        Assert.assertEquals(sentences.size(), mockAbstractTextSentences.size());

        List<String> sentencesArr = sentences.asList();
        for(int i = 0; i < sentences.size(); i++){
            Assert.assertEquals(sentencesArr.get(i), mockAbstractTextSentences.get(i).toLowerCase().trim());
        }
    }

    @Test
    //this test aims to test several special formats generated by coreNLP pre-process
    public void testSentenceSegmentationResults() throws IOException {

        List<String> oldVersionPreprocessedText = FileUtils.readLines(new File("data/OutputFromNLPStanfordTool.txt"), "utf-8");

        ImmutableSet<String> sents = preprocessing.sentenceSegmentation(testSampleText.toString());
        for (String sent : sents) {
            LOG.info(sent);
        }

        List<String> l = sents.asList();

        Assert.assertTrue(oldVersionPreprocessedText.size() == sents.size());

        for (int i = 0; i < sents.size(); i++) {
            Assert.assertFalse(oldVersionPreprocessedText.get(i).toLowerCase().equals(l.get(i)));
            Assert.assertEquals(oldVersionPreprocessedText.get(i).toLowerCase().substring(0, 2)
                    , l.get(i).substring(0, 2));
        }

        Assert.assertTrue(oldVersionPreprocessedText.get(1).contains("-LRB-") && oldVersionPreprocessedText.get(1).contains("-RRB-"));
        Assert.assertFalse(l.get(1).contains("-LRB-") && l.get(1).contains("-RRB-"));
        Assert.assertEquals(oldVersionPreprocessedText.get(1).toLowerCase().substring(0, 6), "acting");
        Assert.assertEquals(l.get(1).substring(0, 6), "acting");
    }

}
