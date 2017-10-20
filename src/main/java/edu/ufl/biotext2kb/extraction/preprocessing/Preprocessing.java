package edu.ufl.biotext2kb.extraction.preprocessing;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import java.util.logging.Logger;

public class Preprocessing extends AbstractModule {

    @Override
    protected void configure(){

    }

    @Inject
    private Logger LOGGER;

    public void sentenceSegmentation(String text) {
        LOGGER.info("here");
    }

}
