package edu.ufl.biotext2kb.utils;

import com.google.common.collect.ImmutableSet;
import edu.ufl.biotext2kb.GuiceJUnit4Runner;
import edu.ufl.biotext2kb.utils.dictionary.BioText2KBEntityAndPredicateDict;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(GuiceJUnit4Runner.class)
public class BioText2KBReadWriteUtilsTest {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BioText2KBReadWriteUtilsTest.class);

    @Test
    public void testReadEntityAndPredicateCSV2DF() throws IOException {
        ImmutableSet<BioText2KBEntityAndPredicateDict> s = BioText2KBReadWriteUtils.
                readEntityAndPredicateCSV2DF("data/samplefortestdict.csv", 0.0, "utf-8");
        for(BioText2KBEntityAndPredicateDict each: s){
            LOG.info(each.toString());
        }

        /*
            the test file has 16 line with two of them are the same
            this assert is aimed to test if the code remove the redundancy and stop words
         */
        Assert.assertEquals(15, s.size());

        // these asserts are aimed to test if the order is correct
        List<BioText2KBEntityAndPredicateDict> al = s.asList();
        Assert.assertEquals(al.get(0).getInstance(), "risk of breast cancer");
        Assert.assertTrue(al.get(1).getWeight() == 3136.4357);
        Assert.assertEquals(al.get(5).isInUMLS(), 1);
        Assert.assertEquals(al.get(2).isEntity(), 0);
        Assert.assertEquals(al.get(al.size()-1).getInstance(), "hcc");
    }

    @Test
    public void testoutputPreprocessedSentences() throws IOException {
        BioText2KBReadWriteUtils.outputPreprocessedSentences("data/Sentencesfrom23Abstracts.txt", "data/testOutput.txt" , "utf-8");

        Set<String> tests = new HashSet<>();

        FileUtils.readLines(new File("data/OutputSample.txt"), "utf-8").forEach(x ->{
            tests.add(x);
        });

        FileUtils.readLines(new File("data/testOutput.txt"), "utf-8").forEach(x -> {
            Assert.assertTrue(tests.contains(x));
        });
    }

    @Test
    public void testGetProperties() throws IOException {

    }
}
