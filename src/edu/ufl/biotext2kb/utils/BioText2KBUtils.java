package edu.ufl.biotext2kb.utils;


import com.google.common.collect.ImmutableSet;
import edu.ufl.biotext2kb.utils.dictionary.BioText2KBEntityAndPredicateDict;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * This class is used for dealing with common operations
 */
public final class BioText2KBUtils {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BioText2KBUtils.class);

    private BioText2KBUtils(){

    }

    /**
     * A method used to read-in the entity and predication dictionary csv file as an ImmutableSet of BioText2KBEntityAndPredicateDict objects
     * Each object in the set is a representation of each row in the csv
     * Each field in BioText2KBEntityAndPredicateDict class is associated with a column in the csv file
     * @param fileName the entity and predicate dictionary csv file name with relative path to the root directory of the project
     * @return An ImmutableSet of BioText2KBEntityAndPredicateDict objects
     * @throws IOException
     */
    public static ImmutableSet<BioText2KBEntityAndPredicateDict> readEntityAndPredicateCSV2DF(String fileName) throws IOException {
        List<BioText2KBEntityAndPredicateDict> arr = new ArrayList<>();
        Iterator<String> itr = FileUtils.readLines(new File(fileName), "utf-8").iterator();
        for(int i = 0; itr.hasNext(); i++) {
            String line = itr.next();
            if(i == 0) continue; //skip the title
            //LOG.info(line);
            String[] fields = line.split(",");
            BioText2KBEntityAndPredicateDict btp = new BioText2KBEntityAndPredicateDict(
                    fields[0],
                    str2Bool(fields[1]),
                    Double.valueOf(fields[2]),
                    str2Bool(fields[3]));
            arr.add(btp);
        }

        //sort the list of objects based on their weight (larger the first)
        arr.sort((o1, o2) -> {
            Double d = o1.getWeight() - o2.getWeight();
            return  d > 0 ? -1 : 1;
        });

        //sort the list of objects based on how many words their instances have (larger the first)
        arr.sort((o1, o2) -> {
            int l = o1.getInstance().split(" ").length - o2.getInstance().split(" ").length;
            return  l > 0 ? -1 : 1;
        });

        return ImmutableSet.copyOf(arr);
    }

    /**
     * A help method to convert the read in 1 or 0 to true or false respectively
     * @param s read-in value which using 1 to represent true and 0 to represent false
     * @return boolean value true or false based on input string s
     */
    private static boolean str2Bool(String s){
        if(Integer.valueOf(s) == 1){
            return true;
        }else if (Integer.valueOf(s) == 0){
            return false;
        }else{
            throw new IllegalArgumentException("Must be 1 or 0 but " + s);
        }
    }


    /**
     *
     * @param stream: a java IO Stream that need to be closed
     */
    public static void closeStream(Closeable stream){
        if (stream != null){
            try {
                stream.close();
            }catch(IOException ex){
                LOG.error(ex.getMessage());
            }
        }
    }

}
