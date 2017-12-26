package edu.ufl.biotext2kb.utils;

import com.google.common.collect.ImmutableSet;
import edu.ufl.biotext2kb.utils.dictionary.BioText2KBEntityAndPredicateDict;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class BioText2KBUtilsTest {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BioText2KBUtilsTest.class);

    @Test
    public void testReadEntityAndPredicateCSV2DF() throws IOException {
        ImmutableSet<BioText2KBEntityAndPredicateDict> s = BioText2KBUtils.readEntityAndPredicateCSV2DF("data/samplefortestdict.csv");
        for(BioText2KBEntityAndPredicateDict each: s){
            LOG.info(each.toString());
        }

        /*
            the test file has 16 line with two of them are the same
            this assert is aimed to test if the code remove the redundancy
         */
        Assert.assertEquals(s.size(), 15);

        // these asserts are aimed to test if the order is correct
        List<BioText2KBEntityAndPredicateDict> al = s.asList();
        Assert.assertEquals(al.get(0).getInstance(), "risk of breast cancer");
        Assert.assertTrue(al.get(1).getWeight() == 3136.4357);
        Assert.assertTrue(al.get(5).isInUMLS());
        Assert.assertFalse(al.get(2).isEntity());
        Assert.assertEquals(al.get(al.size()-1).getInstance(), "hcc");
    }
}
