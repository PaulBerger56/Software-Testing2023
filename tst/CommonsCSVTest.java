import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.FileReader;

public class CommonsCSVTest {

    @Test
    public void testCSVFile() throws Exception {
        FileReader fr = new FileReader("pelldata.csv");
        CSVParser parser = new CSVParser(fr,
                CSVFormat.EXCEL.builder().setIgnoreSurroundingSpaces(true).setHeader().build());


        for(CSVRecord record: parser) {
            String name = record.get("INSTITUTION NAME");
            String award = record.get("TOTAL AWARDS");
            String recipients = record.get("TOTAL RECIPIENTS");

            if(name.equals("GEORGIA GWINNETT COLLEGE")) {
                printInstitutionInfo(name, award, recipients);
            }
            if(name.equals("SAVANNAH COLLEGE OF ART & DESIGN")) {
                printInstitutionInfo(name, award, recipients);
            }

        }
        fr.close();
        parser.close();

    }

    private static void printInstitutionInfo(String name, String award, String recipients) {
        System.out.println(name);
        System.out.println(award);
        System.out.println(recipients);
        int total = Integer.parseInt(award.replace(",","").replace("$","").trim());
        double numStudents = Double.parseDouble(recipients);
        System.out.println("Average aid: " + (total/numStudents));
        System.out.println();
    }
}
