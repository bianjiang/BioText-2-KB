package edu.ufl.biotext2kb.extraction.extractentityandpredicate;

import com.google.common.collect.ImmutableMap;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractEntityAndPredicateTest {
    private ExtractEntityAndPredicate eep = null;
    private static BufferedReader br;
    private static ImmutableSet<String> sampleSents;
    private static final Logger LOG = LoggerFactory.getLogger(ExtractEntityAndPredicateTest.class);

    @BeforeClass
    public static void loadTestSents() {
        try {
            File f = new File("data/Sentencesfrom23Abstracts.txt");
            br = new BufferedReader(new FileReader("data/Sentencesfrom23Abstracts.txt"));
            String line = null;
            ImmutableSet.Builder<String> sentencesTextBuilder = ImmutableSet.<String>builder();

            while ((line = br.readLine()) != null) {
                sentencesTextBuilder.add(line.toLowerCase());
            }

            sampleSents = sentencesTextBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BioText2KBUtils.closeStream(br);
        }
    }

    @Before
    public void createExtractor() {
        eep = new ExtractEntityAndPredicate("data/EntityDict.csv", "data/PredicateDict.csv");
    }

    @Test
    public void testExtractFunc() {
        // create results from current implementation
        ArrayList<ArrayList<String>> thisRess = new ArrayList<>();
        ImmutableMap<String, HashMap<String, ArrayList<String>>> testRes = eep.entitiesPredicatesExtraction(sampleSents);

        /**
         *   this is also a example how to flat the data generated from entitiesPredicatesExtraction function
         *   TODO change this into a public function and add it to the ExtractEntityAndPredicate class for others easy to get the results?
         */
        // using iterator to loop the map
        for(Iterator<Map.Entry<String, HashMap<String, ArrayList<String>>>> itr = testRes.entrySet().iterator(); itr.hasNext();){
            ArrayList<String> thisRes = new ArrayList<>();
            //read in key-value pairs as Map.Entry
            Map.Entry<String, HashMap<String, ArrayList<String>>> pairs = itr.next();

            //get the value (another hashMap)
            HashMap<String, ArrayList<String>> subRes = pairs.getValue();

            //get entities
            subRes.get("entities").forEach(x -> {
                thisRes.add(x);
            });

            //get predicates
            subRes.get("predicates").forEach(x -> {
                thisRes.add(x);
            });

            thisRess.add(thisRes);

            //avoid a ConcurrentModificationException
//            itr.remove();
        }

        //create results from antonio implementation
        ArrayList<ArrayList<String>> antonioRess = new ArrayList<>();
        for (String sent : sampleSents) {
            String antonioRes = search_ent_pred(sent.toLowerCase());
            ArrayList<String> res = new ArrayList<>();
            String[] arr = antonioRes.split(";");
            for (String each : arr) {
                for (String each1 : each.split(",")) {
                    if(!each1.equals("")) {
                        res.add(each1.trim());
                    }
                }
            }
            antonioRess.add(res);
        }

        //assert same results (assume antonio got the right output)

        int count = antonioRess.size();
        LOG.info(String.valueOf(count));
        LOG.info(thisRess.size() + "");
        for(ArrayList<String> alist: thisRess){
            if(antonioRess.contains(alist)){
                count--;
            }

            LOG.info(alist + "");
        }

        LOG.info("*********************************************************************************************************************");

        for(ArrayList<String> slist: antonioRess){
            LOG.info(slist + "");
        }

        LOG.info(count + "");
        Assert.assertEquals(0, count);
    }

    @Test
    public void testEncodingProblemInPubMedSents() {
        HashMap<String, Integer> testMap = new HashMap<>();
        int i = 0;
        for (String each : sampleSents) {
            //System.out.println(each);
            testMap.put(each, i++);
        }

        Assert.assertEquals(sampleSents.size(), testMap.size());
    }

    @Test
    public void testReadInDict() {
//        eep.getEntitiesSet().forEach(x -> {System.out.println(x);});
//        eep.getPredicateSet().forEach(x ->{System.out.println(x);});

        int i = 20;
        Iterator<String> itr = eep.getPredicateSet().iterator();
        while (itr.hasNext()) {
            if (i > 0)
                break;
            else {
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
    public void testReadCsv() {
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
    public void testMatchEntityOrPredicateInSentences() {
        String testEx = "METHODS: In a case-cohort analysis nested within the Women's Health Initiative Observational Study, a prospective cohort of postmenopausal women, baseline plasma samples from 875 incident breast cancer case patients and 839 subcohort participants were tested for levels of seven adipokines, namely leptin, adiponectin, resistin, interleukin-6, tumor necrosis factor-α, hepatocyte growth factor, and plasminogen activator inhibitor-1, and for C-reactive protein (CRP), an inflammatory marker.\n";
        String antonioRes = search_ent_pred(testEx.toLowerCase());
        LOG.info(antonioRes);
        ArrayList<String> res = new ArrayList<>();
        String[] arr = antonioRes.split(";");
        for (String each : arr) {
            for (String each1 : each.split(",")) {
                res.add(each1.trim());
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
        for (ImmutableSortedSet<String> eachDict : dicts) {
            for (String each : eachDict) {
                if (s.contains(" " + each + " ")) {
                    res1.add(each);
                    s = s.replace(" " + each + " ", " ");
                }
            }
        }

        res1.forEach(x -> {
            LOG.info(x.toString());
        });

        Assert.assertEquals(res.size(), res1.size());
        for (int i = 0; i < res.size(); i++) {
            Assert.assertTrue(res.get(i).equals(res1.get(i)));
        }

        Assert.assertTrue(Arrays.deepEquals(res.toArray(), res1.toArray()));
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
