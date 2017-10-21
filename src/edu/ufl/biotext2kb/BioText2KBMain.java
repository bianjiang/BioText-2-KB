package edu.ufl.biotext2kb;

import com.google.inject.Inject;

import java.util.logging.Logger;

public class BioText2KBMain {

    @Inject
    private Logger LOGGER;

    public String start(){
        LOGGER.info("whatever");
        return "whatever";
    }
}
