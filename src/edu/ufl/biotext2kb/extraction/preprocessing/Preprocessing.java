package edu.ufl.biotext2kb.extraction.preprocessing;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import org.slf4j.LoggerFactory;


public class Preprocessing extends AbstractModule {

    @Override
    protected void configure(){

    }

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Preprocessing.class);


    public ImmutableSet<String> sentenceSegmentation(String text) {

        return null;
    }

}
