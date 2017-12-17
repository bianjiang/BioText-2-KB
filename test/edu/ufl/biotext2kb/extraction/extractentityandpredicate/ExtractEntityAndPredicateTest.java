package edu.ufl.biotext2kb.extraction.extractentityandpredicate;

import com.google.common.collect.ImmutableSet;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.ufl.biotext2kb.BioText2KBUtils;
import joinery.DataFrame;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ExtractEntityAndPredicateTest {
    private ExtractEntityAndPredicate eep = null;
    private static BufferedReader br;
    private static ImmutableSet<String> sampleSents;

    @BeforeClass
    public static void loadTestSents(){
        try {
            File f = new File("data/Sentencesfrom23Abstracts.txt");
            br = new BufferedReader(new FileReader("data/Sentencesfrom23Abstracts.txt"));
            String line = null;
            ImmutableSet.Builder<String> sentencesTextBuilder = ImmutableSet.<String>builder();

            while((line = br.readLine()) != null){
                sentencesTextBuilder.add(line);
            }

            sampleSents = sentencesTextBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BioText2KBUtils.closeStream(br);
        }
    }

    @Before
    public void createExtractor(){
        eep = new ExtractEntityAndPredicate("data/EntityDict.csv", "data/PredicateDict.csv");
    }

    @Test
    public void testExtractFunc(){
       
    }

    @Test
    public void testEncodingProblemInPubMedSents(){
        HashMap<String, Integer> testMap = new HashMap<>();
        int i = 0;
        for(String each: sampleSents){
            //System.out.println(each);
            testMap.put(each, i++);
        }

        Assert.assertEquals(sampleSents.size(), testMap.size());
    }

    @Test
    public void testWordTokenizer(){
        for(String each: sampleSents){
            
        }
    }

    @Test
    public void testReadInDict(){
        System.out.println(eep.getEntitiesSet().size());
        System.out.println(eep.getPredicateSet().size());
    }

    @Test
    public void testReadCsv(){
        String fileName = "data/EntityDict.csv";
        File f = new File(fileName);
        Assert.assertTrue(f.exists());
        DataFrame<Object> df = null;
        try {
            df = DataFrame.readCsv(fileName);
            System.out.print(df);
            System.out.println(df.types());
            System.out.println(df.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(df.get(1, "entity"), "breast");
    }

}
