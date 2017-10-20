package edu.ufl.biotext2kb;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.util.logging.Logger;

/*
 * @author Jiang Bian
 */
public class App extends AbstractModule  {

    @Override
    protected void configure(){

    }

    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new App());

        BioText2KB biotext2KB = injector.getInstance(BioText2KB.class);

        biotext2KB.start();

    }
}
