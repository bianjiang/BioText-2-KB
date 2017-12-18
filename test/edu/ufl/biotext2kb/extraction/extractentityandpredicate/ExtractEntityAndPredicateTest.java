package edu.ufl.biotext2kb.extraction.extractentityandpredicate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import edu.ufl.biotext2kb.BioText2KBUtils;
import joinery.DataFrame;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractEntityAndPredicateTest {
    private ExtractEntityAndPredicate eep = null;
    private static BufferedReader br;
    private static ImmutableSet<String> sampleSents;
    private static final Logger LOG = LoggerFactory.getLogger(ExtractEntityAndPredicateTest.class);

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
       for(String sent: sampleSents){
           //res is from antonio code
           String antonioRes = search_ent_pred(sent.toLowerCase());
           ArrayList<String> res = new ArrayList<>();
           String[] arr = antonioRes.split(";");
           for(String each: arr){
               for(String each1: each.split(",")){
                   res.add(each1);
               }
           }

           //res1 is from this new implementation
           ArrayList<String> res1 = new ArrayList<>();
           

           Assert.assertArrayEquals(res.toArray(), res1.toArray());
       }
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
    public void testReadInDict(){
//        eep.getEntitiesSet().forEach(x -> {System.out.println(x);});
//        eep.getPredicateSet().forEach(x ->{System.out.println(x);});

        int i = 20;
        Iterator<String> itr = eep.getPredicateSet().iterator();
        while(itr.hasNext()){
            if(i > 0)
                break;
            else{
                String info = itr.next();
                LOG.info(info);
                Assert.assertTrue(info.length() > i);
            }
            i--;
        }

        Assert.assertEquals(eep.getEntitiesSet().size(), 126934); //TODO need to compare with antonio number
        Assert.assertEquals(eep.getPredicateSet().size(), 3216); //TODO need to compare with antonio number
    }

    @Test
    public void testReadCsv(){
        String fileName = "data/EntityDict.csv";
        File f = new File(fileName);
        Assert.assertTrue(f.exists());
        DataFrame<Object> df = null;
        try {
            df = DataFrame.readCsv(fileName);
            LOG.info(df.toString());
            LOG.info(df.types().toString());
            LOG.info(df.length() + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(df.get(1, "entity"), "breast");
    }

    @Test
    public void testMatchEntityOrPredicateInSentences(){
        String testEx = "METHODS: In a case-cohort analysis nested within the Women's Health Initiative Observational Study, a prospective cohort of postmenopausal women, baseline plasma samples from 875 incident breast cancer case patients and 839 subcohort participants were tested for levels of seven adipokines, namely leptin, adiponectin, resistin, interleukin-6, tumor necrosis factor-α, hepatocyte growth factor, and plasminogen activator inhibitor-1, and for C-reactive protein (CRP), an inflammatory marker.\n";
        String antonioRes = search_ent_pred(testEx.toLowerCase());
        LOG.info(antonioRes);
        ArrayList<String> res = new ArrayList<>();
        String[] arr = antonioRes.split(";");
        for(String each: arr){
            for(String each1: each.split(",")){
                res.add(each1);
            }
        }

        res.forEach(x -> {
            LOG.info(x.toString());
        });

        //code will be used in the private method matchEntityOrPredicateInSentences if pass the test
        //same as antonio, seeking a better way to implement
        String s = " " + testEx.toLowerCase() + " ";
        ArrayList<String> res1 = new ArrayList<>();
        ArrayList<ImmutableSortedSet<String>> dicts = new ArrayList<>();
        dicts.add(eep.getEntitiesSet());
        dicts.add(eep.getPredicateSet());
        for(ImmutableSortedSet<String> eachDict: dicts){
            for(String each: eachDict){
                if(s.contains(" " + each + " ")){
                    res1.add(each);
                    s = s.replace(" " + each + " ", " ");
                }
            }
        }

        res1.forEach(x -> {
            LOG.info(x.toString());
        });

        Assert.assertArrayEquals(res.toArray(), res1.toArray());
    }

    //Antonio method
    private String search_ent_pred(String line) {

        line = line.replace(" '", "'");
        line = " " + line + " ";

        String aux_ent = "";
        String aux_pred = "";
        try {
            for (String al_entitie : eep.getEntitiesSet()) {
                if (line.contains(" " + al_entitie + " ")) {
                    aux_ent = aux_ent + al_entitie + " , ";

                    try {
                        line = line.replace(" " + al_entitie + " ", "  ");
                    } catch (Exception exxx) {
                        System.err.println("search_ent_pred : " + exxx.toString());
                    }
                }
            }

            for (String al_predicate : eep.getPredicateSet()) {
                if (line.contains(" " + al_predicate + " ")) {
                    aux_pred = aux_pred + al_predicate + " , ";

                    try {
                        line = line.replace(" " + al_predicate + " ", "  ");
                    } catch (Exception exxx) {
                        System.err.println("search_ent_pred : " + exxx.toString());
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println("search_ent_pred : " + ex.toString());
        }

        if (!aux_ent.equalsIgnoreCase("")) {
            aux_ent = aux_ent.substring(0, aux_ent.trim().length() - 1);
        }
        if (!aux_pred.equalsIgnoreCase("")) {
            aux_pred = aux_pred.substring(0, aux_pred.trim().length() - 1);
        }

        return aux_ent + " ; " + aux_pred;
    }

}
