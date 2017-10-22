package edu.ufl.biotext2kb.pubmed;

import com.google.inject.Inject;
import edu.ufl.biotext2kb.GuiceJUnit4Runner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(GuiceJUnit4Runner.class)
public class PubMedTest {

    private static final Logger LOG = LoggerFactory.getLogger(PubMedTest.class);

    @Inject
    private PubMed pubMed;

    @Test
    public void testFetchAbstractByPMID() {

        try {
            String result = pubMed.fetchAbstractTextByPMID("26084689");
            LOG.debug(result);

        }catch (Exception ex){
            //
        }
    }
}
