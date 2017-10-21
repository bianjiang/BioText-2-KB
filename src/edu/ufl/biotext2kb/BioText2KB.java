package edu.ufl.biotext2kb;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.PrintWriter;
import java.util.Arrays;
import static java.lang.System.out;
/*
 * @author Jiang Bian
 */
public class BioText2KB extends AbstractModule  {


    private final static String VERBOSE_OPTION = "verbose";
    private final static String FILE_OPTION = "file";

    public static void main(final String[] args) {

        final Options options = generateOptions();

        if (args.length < 1)
        {
            printUsage(options);
            printHelp(options);
            System.exit(-1);
        }

        final CommandLine commandLine = generateCommandLine(options, args);


        // "interrogation" stage of processing with Apache Commons CLI
        if (commandLine != null)
        {
            final boolean verbose =
                    commandLine.hasOption(VERBOSE_OPTION);
            final String fileName =
                    commandLine.getOptionValue(FILE_OPTION);
            out.println("The file '" + fileName + "' was provided and verbosity is set to '" + verbose + "'.");
        }

        Injector injector = Guice.createInjector(new BioText2KB());

        BioText2KBMain biotext2KBMain = injector.getInstance(BioText2KBMain.class);

        biotext2KBMain.start();

    }


    /**
     * "Definition" stage of command-line parsing with Apache Commons CLI.
     * @return Definition of command-line options.
     */
    private static Options generateOptions()
    {
        final Option verboseOption = Option.builder("v")
                .required(false)
                .hasArg(false)
                .longOpt(VERBOSE_OPTION)
                .desc("Print status with verbosity.")
                .build();
        final Option fileOption = Option.builder("f")
                .required()
                .longOpt(FILE_OPTION)
                .hasArg()
                .desc("File to be processed.")
                .build();
        final Options options = new Options();
        options.addOption(verboseOption);
        options.addOption(fileOption);
        return options;
    }

    /**
     * "Parsing" stage of command-line processing demonstrated with
     * Apache Commons CLI.
     *
     * @param options Options from "definition" stage.
     * @param commandLineArguments Command-line arguments provided to application.
     * @return Instance of CommandLine as parsed from the provided Options and
     *    command line arguments; may be {@code null} if there is an exception
     *    encountered while attempting to parse the command line options.
     */
    private static CommandLine generateCommandLine(
            final Options options, final String[] commandLineArguments)
    {
        final CommandLineParser cmdLineParser = new DefaultParser();
        CommandLine commandLine = null;
        try
        {
            commandLine = cmdLineParser.parse(options, commandLineArguments);
        }
        catch (ParseException parseException)
        {
            out.println(
                    "ERROR: Unable to parse command-line arguments "
                            + Arrays.toString(commandLineArguments) + " due to: "
                            + parseException);
        }
        return commandLine;
    }

    /**
     * Generate usage information with Apache Commons CLI.
     *
     * @param options Instance of Options to be used to prepare
     *    usage formatter.
     * @return HelpFormatter instance that can be used to print
     *    usage information.
     */
    private static void printUsage(final Options options)
    {
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "biotext2kb";
        out.println("\n=====");
        out.println("USAGE");
        out.println("=====");
        final PrintWriter pw  = new PrintWriter(out);
        formatter.printUsage(pw, 80, syntax, options);
        pw.flush();
    }

    /**
     * Generate help information with Apache Commons CLI.
     *
     * @param options Instance of Options to be used to prepare
     *    help formatter.
     * @return HelpFormatter instance that can be used to print
     *    help information.
     */
    private static void printHelp(final Options options)
    {
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "biotext2kb";
        final String usageHeader = "Example of Using Apache Commons CLI";
        final String usageFooter = "See https://github.com/bianjiang/BioText-2-KB/ for further details.";
        out.println("\n====");
        out.println("HELP");
        out.println("====");
        formatter.printHelp(syntax, usageHeader, options, usageFooter);
    }

    @Override
    protected void configure(){

    }
}
