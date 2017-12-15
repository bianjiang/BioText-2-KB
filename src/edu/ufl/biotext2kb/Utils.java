package edu.ufl.biotext2kb;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 */
public final class Utils {

    private Utils(){

    }

    public static void closeStream(Closeable stream){
        if (stream != null){
            try {
                stream.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
