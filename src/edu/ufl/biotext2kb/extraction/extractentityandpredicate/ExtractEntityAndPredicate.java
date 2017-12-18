package edu.ufl.biotext2kb.extraction.extractentityandpredicate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.AbstractModule;
import edu.ufl.biotext2kb.BioText2KBUtils;
import joinery.DataFrame;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ExtractEntityAndPredicate extends AbstractModule {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ExtractEntityAndPredicate.class);

    private ImmutableSortedSet<String> entitiesSet;
    private ImmutableSortedSet<String> predicateSet;

    //TODO replace the stop-words below with  with dynamic detection implementation
    //*********************************************************************************************************************************************
    public static HashSet<String> stop_words_entities_ = new HashSet<>(Arrays.asList("normal", "intake", "intakes", "known",
            "preventing", "has", "have", "influences", "influence", "identified", "decreases", "associated", "associated with", "not", "acts", "act",
            "decrease", "high", "low", "lower", "higher", "other", "on", "affect", "affects", "i", "ii", "iii", "is", "a", "46", "24", "use", "uses"
            , "targeting", "or", "25", "be", "part", "parts", "pre", "light", "novel", "none", "all", "total", "with", "without", "reported", "half", "benefits", "benefit", "changes", "change",
            "new", "be", "are", "as", "at", "and", "this", "well", "may", "increase", "share", "related", "high", "associated", "links", "play", "plays", "help", "helps"));
    public static HashSet<String> stop_words_predicates_ = new HashSet<>(Arrays.asList("under", "now", "well", "while", "ai", "ua", "'s", "-"));
    //*********************************************************************************************************************************************


    public ExtractEntityAndPredicate(String entityFileName, String predicatefileName) {
        this.entitiesSet = loadEntityOrPredicateSet(entityFileName, stop_words_entities_);
        this.predicateSet = loadEntityOrPredicateSet(predicatefileName, stop_words_predicates_);
    }


    /**
     * The function will scan every sentence from the input and extract entities and predicates in each of them based on pre-defined dictionaries
     * The results are stored as a Map structure which can be easy to handle as a json
     * The key is the original sentence
     * The value is a hashmap with keys as 'entities' and 'predicates' and lists of instances as values
     * A example of returned map showed as follow in json format:
     *  {
     *      "I want to eat apple, you hates oranges":
     *      {
     *              entities: [I, you, apple, oranges],
     *              predicates: [eat, hates]
     *      }
     * }
     *
     * @param sents a set of sentences obtained from abstracts of PreMed publications
     * @return a Immutable map with entities-predicates pairs extracted from each sentences
     */
    public ImmutableMap<String, HashMap<String, ArrayList<String>>> entitiesPredicatesExtraction(ImmutableSet<String> sents) {

        ImmutableMap.Builder<String, HashMap<String, ArrayList<String>>> entityPredicatePairs = ImmutableMap.builder();

        for (String eachSent : sents) {
            //TODO why there is a replace function here? should it be in the preprocess?
            eachSent = " " + eachSent.replace(" '", "'") + " ";

            HashMap<String, ArrayList<String>> entityAndPredicateInOneSent = new HashMap<>();

            //extract entities from the current sentence
            entityAndPredicateInOneSent.put("entities", matchEntityOrPredicateInSentences(eachSent, true));

            //extract predicates from the current sentence
            //TODO bug here, eachSent not contain the modification from previous entity function
            entityAndPredicateInOneSent.put("predicates", matchEntityOrPredicateInSentences(eachSent, false));

            //add the sentence (K) with entities and predicates (V) in it to a map
            entityPredicatePairs.put(eachSent, entityAndPredicateInOneSent);
        }

        return entityPredicatePairs.build();
    }


    /**
     * @param line     each sentence from input
     * @param isEntity if true return a list of all entities inside the sentence else return a list of all predicates in the sentence
     * @return a list of instances that in the sentence and also matched in the entity or predicate dictionary
     */
    private ArrayList<String> matchEntityOrPredicateInSentences(String line, boolean isEntity) {
        ImmutableSortedSet<String> dict = null;

        if (isEntity) {
            dict = this.entitiesSet;
        } else {
            dict = this.predicateSet;
        }

        //match instances from the dict in the sentence, try matching the longest one first then shorter one.
        ArrayList<String> instances = new ArrayList<>();
        for(String inDict: dict){
            String target = " " + inDict + " ";
            if(line.contains(target)){
                line = line.replace(target, " ");
                instances.add(inDict);
            }
        }

        return instances;
    }


    /**
     * the function aims to read-in the predefined entities and predicates to help extract these from the sentences generated from pre-processing
     *
     * @param fileName entity or predicate pre-extracted dictionary
     * @return a set of entities or predicates
     */
    private ImmutableSortedSet<String> loadEntityOrPredicateSet(String fileName, HashSet<String> stopWords) {
        //sort all entities or predicates based on their length
        SortedSet<String> entityOrPredicateDictBuilder = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                if (s1.equals(s2)) return 0;
                //use word length to sort
                if (s1.split(" ").length > s2.split(" ").length) return -1;
                else return 1;
            }
        });

        BufferedReader br = null;
        String line;
        String word;

        try {
            br = new BufferedReader(new FileReader(fileName));

            while ((line = br.readLine()) != null) {
                //the first field in the read-in csv file is entity or predicate
                word = line.split(",")[0];
                //remove the entity or predicate word in stop words list since we do not want to include them
                if (!stopWords.contains(word)) {
                    entityOrPredicateDictBuilder.add(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BioText2KBUtils.closeStream(br);
        }

        System.out.println(entityOrPredicateDictBuilder);

        //return as immutableSet
        return ImmutableSortedSet.copyOfSorted(entityOrPredicateDictBuilder);
    }


    /**
     * The function aims to read in the data from csv file as a dataframe
     *
     * @param fileName the csv file need to be loaded in
     * @return a dataframe created by reading in a csv file
     */
    public DataFrame<Object> loadEntityOrPredicateDataFrame(String fileName) {
        //this is df is equivalent to dataframe from pandas in python
        DataFrame<Object> df = null;

        try {
            df = DataFrame.readCsv(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return df;
    }


    /**
     * @return entities set
     */
    public ImmutableSortedSet<String> getEntitiesSet() {
        return entitiesSet;
    }


    /**
     * @return predicate Set
     */
    public ImmutableSortedSet<String> getPredicateSet() {
        return predicateSet;
    }


    @Override
    protected void configure() {

    }
}
