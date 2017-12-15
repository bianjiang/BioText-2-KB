package edu.ufl.biotext2kb.extraction.extractentityandpredicate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExtractEntityAndPredicateTest {
    private ExtractEntityAndPredicate eep = null;

    @Before
    private void createExtractor(){
        eep = new ExtractEntityAndPredicate();
    }

    @Test
    public void testExtractFunc(){
        String predFile = "";
        String entFile = "";

        eep.setEntityFileName(entFile);
        eep.setPredicateFileName(predFile);
    }

}
