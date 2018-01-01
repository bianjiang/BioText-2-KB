package edu.ufl.biotext2kb.utils;


import com.github.racc.tscg.TypesafeConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.ufl.biotext2kb.extraction.preprocessing.Preprocessing;
import edu.ufl.biotext2kb.utils.dictionary.BioText2KBEntityAndPredicateDict;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * This class is used for dealing with common operations on file reading and writing
 */
public final class BioText2KBReadWriteUtils {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BioText2KBReadWriteUtils.class);
    private static Preprocessing preprocessing = new Preprocessing();

    private BioText2KBReadWriteUtils() {

    }

    //TODO replace the stop-words below with  with dynamic detection implementation
    //*********************************************************************************************************************************************
    public static HashSet<String> stop_words_entities_ = new HashSet<>(Arrays.asList("normal", "intake", "intakes", "known",
            "preventing", "has", "have", "influences", "influence", "identified", "decreases", "associated", "associated with", "not", "acts", "act",
            "decrease", "high", "low", "lower", "higher", "other", "on", "affect", "affects", "i", "ii", "iii", "is", "a", "46", "24", "use", "uses"
            , "targeting", "or", "25", "be", "part", "parts", "pre", "light", "novel", "none", "all", "total", "with", "without", "reported", "half", "benefits", "benefit", "changes", "change",
            "new", "be", "are", "as", "at", "and", "this", "well", "may", "increase", "share", "related", "high", "associated", "links", "play", "plays", "help", "helps"));
    public static HashSet<String> stop_words_predicates_ = new HashSet<>(Arrays.asList("under", "now", "well", "while", "ai", "ua", "'s", "-"));
    //*********************************************************************************************************************************************

    /**
     *
     * @param stopWordsFile stop-words file (csv format)
     * @param encoding
     * @return A set of tuples which contains two elements: 1. stop word; 2. indicator for if it is entity-stop-word or predicate-stop-word
     * @throws IOException
     */
    public static ImmutableSet<ImmutableList<String>> loadStopWords(String stopWordsFile, String encoding) throws IOException{
        ImmutableSet.Builder<ImmutableList<String>> stopWords = ImmutableSet.builder();

        Iterator<String> itr = FileUtils.readLines(new File(stopWordsFile), encoding).iterator();
        for (int i = 0; itr.hasNext(); i++) {
            List<String> temp = new ArrayList<>();
            String line = itr.next();
            if (i == 0) continue; //skip the title
            //LOG.info(line);
            String[] fields = line.split(",");
            temp.add(fields[0]);
            temp.add((fields[1]));
            stopWords.add(ImmutableList.copyOf(temp));
        }

        return stopWords.build();
    }

    /**
     A method used to read-in the entity and predication dictionary csv file as an ImmutableSet of BioText2KBEntityAndPredicateDict objects
     * Each object in the set is a representation of each row in the csv
     * Each field in BioText2KBEntityAndPredicateDict class is associated with a column in the csv file
     * @param entityPredicateFileName the entity and predicate dictionary csv file name with relative path to the root directory of the project
     * @param stopWordsFile the file provides the stop words that need to be filtered off from the dictionary
     * @param cutOff the weight value used to filtered off the entities and predicates whose weight is lower than this standard
     * @param encoding the encoding format
     * @return An ImmutableSet of BioText2KBEntityAndPredicateDict objects
     * @throws IOException
     */
    public static ImmutableSet<BioText2KBEntityAndPredicateDict> readEntityAndPredicateCSV2DF(String entityPredicateFileName, String stopWordsFile, Double cutOff, String encoding) throws IOException {
        List<BioText2KBEntityAndPredicateDict> arr = new ArrayList<>();

        //load the stop words list
        //************************************
        //TODO not include in the code yet
        ImmutableSet<ImmutableList<String>> stopWordsDict = loadStopWords(stopWordsFile, encoding);
        //************************************

        //load the entity and predicate dictionary file
        Iterator<String> itr = FileUtils.readLines(new File(entityPredicateFileName), encoding).iterator();
        for (int i = 0; itr.hasNext(); i++) {
            String line = itr.next();
            if (i == 0) continue; //skip the title
            //LOG.info(line);
            String[] fields = line.split(",");

            //TODO update the stopwords filter method
            if (!(Double.valueOf(fields[2]) <= cutOff && Integer.valueOf(fields[3]) == 1) &&
                    !(stop_words_entities_.contains(fields[0]) && Integer.valueOf(fields[3]) == 1) &&
                    !(stop_words_predicates_.contains(fields[0]) && Integer.valueOf(fields[3]) == 0)) {
                BioText2KBEntityAndPredicateDict btp = new BioText2KBEntityAndPredicateDict(
                        fields[0],
                        Integer.valueOf(fields[1]),
                        Double.valueOf(fields[2]),
                        Integer.valueOf(fields[3]));
                arr.add(btp);
            }
        }

        //sort the list of objects based on their weight (larger the first)
        Collections.sort(arr, new Comparator<BioText2KBEntityAndPredicateDict>() {
            @Override
            public int compare(BioText2KBEntityAndPredicateDict o1, BioText2KBEntityAndPredicateDict o2) {
                Double d = o1.getWeight() - o2.getWeight();
                if (d != 0) return d > 0 ? -1 : 1;
                else return 0;
            }
        });

        //sort the list of objects based on how many words their instances have (larger the first)
        Collections.sort(arr, new Comparator<BioText2KBEntityAndPredicateDict>() {
            @Override
            public int compare(BioText2KBEntityAndPredicateDict o1, BioText2KBEntityAndPredicateDict o2) {
                int l = o1.getInstance().split(" ").length - o2.getInstance().split(" ").length;
                if (l != 0) return l > 0 ? -1 : 1;
                else return 0;
            }
        });

        return ImmutableSet.copyOf(arr);
    }

    /**
     * This function is used to output the sentences after pre-processing step
     * @param inputFileName The original file contains all the text for pre-processing
     * @param outputFileName The file which stores all the text after pre-processing
     * @param encoding An optional parameter, if not provide using utf-8 else using the provided coding method
     * @throws IOException
     */
    public static void outputPreprocessedSentences(String inputFileName, String outputFileName, String encoding) throws IOException {
        LOG.info("Using " + encoding + " to encode all the text.");

        Iterator<String> itr = FileUtils.readLines(new File(inputFileName), encoding).iterator();
        StringBuilder testSampleText = new StringBuilder();

        for (; itr.hasNext(); ) {
            testSampleText.append(itr.next());
            testSampleText.append("\n");
        }

        FileUtils.writeLines(
                new File(outputFileName),
                encoding,
                Collections.unmodifiableList(preprocessing.sentenceSegmentation(testSampleText.toString()).asList()),
                "\n");
    }
}
