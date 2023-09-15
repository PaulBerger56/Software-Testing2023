import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.StringReader;
import java.net.URL;
import java.util.TreeMap;

public class GunViolenceTest {

    public static final String GUN_URL = "https://www.gunviolencearchive.org/export-finished/download?uuid=1e1fc646-a270-420e-8b41-3f497794831f&filename=public%3A//export-1744b729-ac79-4495-9e86-ce013c8c1b5e.csv";

    @Test
    public void testLast72Hours() throws Exception {
        String str = IOUtils.toString(new URL(GUN_URL), "UTF-8");
        CSVParser parser = new CSVParser(new StringReader(str),
                CSVFormat.DEFAULT.builder().setHeader().setIgnoreSurroundingSpaces(true).build());

        TreeMap<String, Integer> counter = new TreeMap<>();

        for(CSVRecord record: parser) {
            String state = record.get("State");
            if(counter.containsKey(state)) {
                int count = counter.get(state);
                counter.put(state, count+1);
            } else {
                counter.put(state,1);
            }
        }
        System.out.println(counter);
        parser.close();
    }
}
