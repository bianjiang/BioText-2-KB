package edu.ufl.biotext2kb.extraction.extractentityandpredicate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.AbstractModule;
import edu.ufl.biotext2kb.utils.BioText2KBUtils;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * This class is designed for extraction of information (entities and predicates) from preprocessed sentences
 * The extraction procedure is based on dictionary matching method
 * Only the entities and predicates in the dictionary can be extracted from the sentences
 */
public class ExtractEntityAndPredicate extends AbstractModule {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ExtractEntityAndPredicate.class);

    private String entityPredicateDictFile;
    private String sent;

    //TODO replace the stop-words below with  with dynamic detection implementation
    //*********************************************************************************************************************************************
    public static HashSet<String> stop_words_entities_ = new HashSet<>(Arrays.asList("normal", "intake", "intakes", "known",
            "preventing", "has", "have", "influences", "influence", "identified", "decreases", "associated", "associated with", "not", "acts", "act",
            "decrease", "high", "low", "lower", "higher", "other", "on", "affect", "affects", "i", "ii", "iii", "is", "a", "46", "24", "use", "uses"
            , "targeting", "or", "25", "be", "part", "parts", "pre", "light", "novel", "none", "all", "total", "with", "without", "reported", "half", "benefits", "benefit", "changes", "change",
            "new", "be", "are", "as", "at", "and", "this", "well", "may", "increase", "share", "related", "high", "associated", "links", "play", "plays", "help", "helps"));
    public static HashSet<String> stop_words_predicates_ = new HashSet<>(Arrays.asList("under", "now", "well", "while", "ai", "ua", "'s", "-"));
    //*********************************************************************************************************************************************


    public ExtractEntityAndPredicate() {
        LOG.info("The entity and predicate dictionary is loaded.");
    }


    /**
     * The function will scan every sentence from the input and extract entities and predicates in each of them based on pre-defined dictionaries
     * The results are stored as a Map structure which can be easy to handle as a json
     * The key is the original sentence
     * The value is a hashmap with keys as 'entities' and 'predicates' and lists of instances as values
     * A example of returned map showed as follow in json format:
     * {
     * "I want to eat apple, you hates oranges":
     * {
     * entities: (I, you, apple, oranges),
     * predicates: (eat, hates)
     * }
     * }
     *
     * @param sents a set of sentences obtained from abstracts of PreMed publications
     * @return a Immutable map with entities-predicates pairs extracted from each sentences
     */
    public ImmutableMap<String, ImmutableMap<String, ImmutableSet<String>>> extractEntitiesPredicates(ImmutableSet<String> sents) {

        ImmutableMap.Builder<String, ImmutableMap<String, ImmutableSet<String>>> entityPredicatePairs = ImmutableMap.builder();

        for (String eachSent : sents) {
            //TODO why there is a replace function here? should it be in the preprocess?
            eachSent = " " + eachSent.replace(" '", "'") + " ";
            this.sent = eachSent;

            ImmutableMap.Builder<String, ImmutableSet<String>> entityAndPredicateInOneSent = ImmutableMap.builder();

            //extract entities from the current sentence
            //TODO remember to extract entity first then predicate so we need to loop 2 twice consecutive with each found word been removed
            entityAndPredicateInOneSent.put("entities", matchEntityPredicateInSentences(this.sent, true));

            //add the sentence (K) with entities and predicates (V) in it to a map
            entityPredicatePairs.put(eachSent, entityAndPredicateInOneSent.build());
        }

        return entityPredicatePairs.build();
    }


    /**
     * @param line     each sentence from input
     * @param isEntity if true return a list of all entities inside the sentence else return a list of all predicates in the sentence
     * @return a list of instances that in the sentence and also matched in the entity or predicate dictionary
     */
    private ImmutableSet<String> matchEntityPredicateInSentences(String line, boolean isEntity) {
        ImmutableSortedSet<String> dict = null;

        //match instances from the dict in the sentence, try matching the longest one first then shorter one.
        ImmutableSet.Builder<String> instances = ImmutableSet.builder();
        for (String inDict : dict) {
            String target = " " + inDict + " ";
            if (line.contains(target)) {
                line = line.replace(target, " ");
                instances.add(inDict);
            }
        }

        return instances.build();
    }


    @Deprecated
    /**
     * the function aims to read-in the predefined entities and predicates to help extract these from the sentences generated from pre-processing
     * the function is deprecated due to the sorted function is insufficient to fulfill the requirement, use BioText2KBUtils.loadCsv2DataFrame
     *
     * @param fileName entity or predicate pre-extracted dictionary
     * @return a set of entities or predicates
     */
    private ImmutableSortedSet<String> loadDictionary(String fileName, HashSet<String> stopWords) {
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

        //return as immutableSet
        return ImmutableSortedSet.copyOfSorted(entityOrPredicateDictBuilder);
    }


    @Override
    protected void configure() {
        //create a property file for configuration such as where to load the dictionary
        Properties prop = new Properties();
        try {
            prop.load(new FileReader("BioText2KB.properties"));
            this.entityPredicateDictFile = prop.getProperty("entitypreidcateDict");
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }
    }
}
