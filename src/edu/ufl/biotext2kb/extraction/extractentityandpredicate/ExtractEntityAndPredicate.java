package edu.ufl.biotext2kb.extraction.extractentityandpredicate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import edu.ufl.biotext2kb.utils.BioText2KBUtils;
import edu.ufl.biotext2kb.utils.dictionary.BioText2KBEntityAndPredicateDict;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 * This class is designed for extraction of information (entities and predicates) from preprocessed sentences
 * The extraction procedure is based on dictionary matching method
 * Only the entities and predicates in the dictionary can be extracted from the sentences
 */
public class ExtractEntityAndPredicate extends AbstractModule {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ExtractEntityAndPredicate.class);
    private ImmutableSet<BioText2KBEntityAndPredicateDict> entityPredicateDict;

    public ExtractEntityAndPredicate() throws IOException {
        entityPredicateDict = BioText2KBUtils.readEntityAndPredicateCSV2DF("data/dictionary.csv", 30.0);
        LOG.info("The entity and predicate dictionary is loaded.");
    }

    /**
     * The function will scan every sentence from the input and extract entities and predicates in each of them based on pre-defined dictionaries
     * The results are stored as a Map structure which can be easy to handle as a json
     * The key is the original sentence
     * The value is a hashmap with keys as 'entities' and 'predicates' and lists of instances as values
     * A example of returned map showed as follow in json format:
     * ____{
     * ______"I want to eat apple, you hates oranges":
     * ________{
     * __________entities: (I, you, apple, oranges),
     * __________predicates: (eat, hates)
     * ________},
     * ______"I want to take a bite of banana, you take care of a cat":
     * ________{
     * __________entities: (I, you, banana, cat),
     * __________predicates: (take a bite of, take care of)
     * ________},
     * ________....
     * ____}
     *
     * @param sents a set of sentences obtained from abstracts of PreMed publications
     * @return a Immutable map with entities-predicates pairs extracted from each sentences
     */
    public ImmutableMap<String, ImmutableMap<String, ImmutableSet<String>>> extractEntitiesPredicates(ImmutableSet<String> sents) {
        ImmutableMap.Builder<String, ImmutableMap<String, ImmutableSet<String>>> entityPredicatePairs = ImmutableMap.builder();

        for (String eachSent : sents) {
            ImmutableMap.Builder<String,  ImmutableSet<String>> extractedTerms = ImmutableMap.builder();

            //extract entities
            ReturnTwoThings resEntity = matchEntityPredicateInSentence(eachSent, 1);
            eachSent = resEntity.getModifiedSentence();
            extractedTerms.put("entitis", resEntity.getMatchedTerms());

            //extract predicates
            ReturnTwoThings resPredicate = matchEntityPredicateInSentence(eachSent, 0);
            extractedTerms.put("predicates", resEntity.getMatchedTerms());

            //add the sentence (K) with entities and predicates (V) in it to a map
            entityPredicatePairs.put(eachSent, extractedTerms.build());
        }

        return entityPredicatePairs.build();
    }

    /**
     * This function will match the terms in the sentence with the terms in the dictionary and extract them
     * @param line The input sentence in which we need to extract the entities and predicates
     * @param isEntity if this equals to 1, we perform Entity extraction; if this equals to 0, we perform predicate extraction (only 0 and 1 are allowed)
     * @return The function return a object which has two fields. One is extracted terms and the other is updated sentences (remove the terms extracted)
     */
    private ReturnTwoThings matchEntityPredicateInSentence(String line, int isEntity) {
        ImmutableSet.Builder<String> terms = ImmutableSet.builder();

        if(!line.startsWith(" ") || !line.endsWith(" ")) { line = " " + line + " "; }

        for(BioText2KBEntityAndPredicateDict each: entityPredicateDict){
            if(each.isEntity() == isEntity){
                String target = " " + each + " ";
                if(line.contains(target)){
                    line = line.replace(target, " ");
                    terms.add(target);
                }
            }
        }

        return new ReturnTwoThings(terms.build(), line);
    }

    @Override
    protected void configure() {

    }

    /**
     * This class is a helper class for returning two different types of values from a function
     */
    private class ReturnTwoThings {
        private ImmutableSet<String> matchedTerms;
        private String modifiedSentence;

        private ReturnTwoThings(ImmutableSet<String> matchedTerms, String modifiedSentence){
            this.matchedTerms = matchedTerms;
            this.modifiedSentence = modifiedSentence;
        }

        private ImmutableSet<String> getMatchedTerms() {
            return matchedTerms;
        }

        private String getModifiedSentence() {
            return modifiedSentence;
        }
    }
}
