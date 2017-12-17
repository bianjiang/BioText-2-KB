package edu.ufl.biotext2kb;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class is used for dealing with common operations
 */
public final class BioText2KBUtils {

    private BioText2KBUtils(){

    }

    /**
     * each hashMap using fields from csv headers as keys and fields associated values as values
     * @param fileName: csv file need to be converted into a set of hashMaps
     * @return a set of all csv content
     */
    public static synchronized ImmutableSet<HashMap<String, String>> readCsvFile(String fileName){
        ImmutableSet.Builder<HashMap<String, String>> dictionary = ImmutableSet.builder();



        return dictionary.build();
    }

    /**
     *
     * @param stream: a java IO Stream that need to be closed
     */
    public static synchronized void closeStream(Closeable stream){
        if (stream != null){
            try {
                stream.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }

}
