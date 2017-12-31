package edu.ufl.biotext2kb.extraction.extractentityandpredicate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import edu.ufl.biotext2kb.extraction.preprocessing.Preprocessing;
import edu.ufl.biotext2kb.GuiceJUnit4Runner;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(GuiceJUnit4Runner.class)
public class ExtractEntityAndPredicateTest {
    private static final Logger LOG = LoggerFactory.getLogger(ExtractEntityAndPredicateTest.class);
    private static ImmutableSet<String> sentences;

    @Inject
    private Preprocessing preprocessing;

    @Inject
    private ExtractEntityAndPredicate eeap;

    @Before
    public void loadTestSents() throws IOException {
        Iterator<String> itr = FileUtils.readLines(new File("data/Sentencesfrom23Abstracts.txt"), "utf-8").iterator();
        StringBuilder testSampleText = new StringBuilder();

        for (; itr.hasNext(); ) {
            testSampleText.append(itr.next());
            testSampleText.append("\n");
        }

        sentences = preprocessing.sentenceSegmentation(testSampleText.toString());
    }

    @Test
    public void extractEntitiesPredicates() {
        ImmutableMap<String, ImmutableMap<String, ImmutableSet<String>>> testRes = eeap.extractEntitiesPredicates(sentences);
        Iterator<Map.Entry<String, ImmutableMap<String, ImmutableSet<String>>>> itr = testRes.entrySet().iterator();
        for(;itr.hasNext();){
            Map.Entry<String, ImmutableMap<String, ImmutableSet<String>>> en = itr.next();
            LOG.info(en.getKey());
            LOG.info(en.getValue().toString());
            LOG.info("*****************************");
        }
    }

    @Test
    public void testFiltering(){
        String sample = "MR-derived IHF correlated with D-IHF (rho: 0.626; p = 0.0001), 30 g case-control study but levels of agreement deviated in upper range values (CS-MR over-estimated IHF: regression versus zero, p = 0.009) abc.";
        sample = sample.toLowerCase();
        List<String> dict = new ArrayList<>(Arrays.asList("0.0001", "rho", "0.626", "ihf", "p", "control study", "abc", "case-control study", "g", "mr-derived", "but"));
        for(String each: dict) {
            String target0 = each + "\\p{Punct}";
            String target1 = " " + each + " ";
            String target2 = "^" + each;
            String target = target0 + "|" + target1 + "|" + target2;

            Pattern p = Pattern.compile(target);
            Matcher m = p.matcher(sample);
            if(m.find()){
                System.out.println("find: " + each);
                sample = sample.replaceFirst(each, " ");
                System.out.println(sample);
            }
        }
    }
}
