package edu.ufl.biotext2kb.extraction.extractentityandpredicate;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import edu.ufl.biotext2kb.extraction.preprocessing.Preprocessing;
import edu.ufl.biotext2kb.utils.BioText2KBUtils;
import edu.ufl.biotext2kb.GuiceJUnit4Runner;
import edu.ufl.biotext2kb.utils.dictionary.BioText2KBEntityAndPredicateDict;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
    public void testLoadCsv2DF() {

    }

}
