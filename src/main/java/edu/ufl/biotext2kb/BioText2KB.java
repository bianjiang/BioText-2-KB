package edu.ufl.biotext2kb;

import com.google.inject.Inject;

import java.util.logging.Logger;

public class BioText2KB {

    @Inject
    private Logger LOGGER;

    public void start(){
        LOGGER.info("whatever");
    }
}
