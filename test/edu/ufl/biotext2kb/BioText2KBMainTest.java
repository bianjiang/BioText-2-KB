/*
 * This Java source file was generated by the Gradle 'init' task.
 */

package edu.ufl.biotext2kb;

import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

@RunWith(GuiceJUnit4Runner.class)
public class BioText2KBMainTest {

    private static final Logger LOG = LoggerFactory.getLogger(BioText2KBMainTest.class);

    @Inject
    private BioText2KBMain bioText2KBMain;

    @Test
    public void testBioText2KBMainHasAStart() {
        assertNotNull("whatever", bioText2KBMain.start());
    }
}
