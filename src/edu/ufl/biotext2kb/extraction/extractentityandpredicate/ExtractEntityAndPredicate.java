package edu.ufl.biotext2kb.extraction.extractentityandpredicate;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;

import java.util.HashSet;

public class ExtractEntityAndPredicate extends AbstractModule {
    String entityFileName = null;
    String predicateFileName = null;
    HashSet<String> alSentences = null;

    /**
     * empty constructor
     */
    public ExtractEntityAndPredicate(){

    }

    /**
     * constructor taking entity and predicate dictionary files
     * @param entFileName
     * @param predFileName
     */
    public ExtractEntityAndPredicate(String entFileName, String predFileName){
        this.entityFileName = entFileName;
        this.predicateFileName = predFileName;
    }

    /**
     *
     * @param line
     * @return
     */
    public String searchEntityOrPredicate(String line){

        return null;
    }

    public

    public void setEntityFileName(String entityFileName) {
        this.entityFileName = entityFileName;
    }

    public void setPredicateFileName(String predicateFileName) {
        this.predicateFileName = predicateFileName;
    }

    public String getEntityFileName() {
        return entityFileName;
    }

    public String getPredicateFileName() {
        return predicateFileName;
    }

    @Override
    protected void configure() {

    }
}
