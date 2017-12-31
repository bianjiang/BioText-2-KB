package edu.ufl.biotext2kb;

import com.github.racc.tscg.TypesafeConfigModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.internal.ProviderMethodsModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.io.File;

public class GuiceJUnit4Runner extends BlockJUnit4ClassRunner {

    public GuiceJUnit4Runner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public Object createTest() throws Exception {

        Object object = super.createTest();

        Module module = ProviderMethodsModule.forObject(object);

        AbstractModule m1 = new AbstractModule() {
            @Override
            protected void configure() {
                Config cf = ConfigFactory.parseFile(new File("application.conf"));
                install(TypesafeConfigModule.fromConfigWithPackage(cf,""));
            }
        };

        Guice.createInjector(module, m1).injectMembers(object);
        return object;
    }

}