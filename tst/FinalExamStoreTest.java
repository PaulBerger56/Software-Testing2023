import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

public class FinalExamStoreTest {

    private static String url = "https://www.cheapshark.com/api/1.0/stores";
    private static JsonNode root;

    @BeforeClass
    public static void setUp() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        root =  mapper.readTree(new URL(url));
    }
    @Test
    public void nameNotEmptyTest() throws IOException {
        for(JsonNode j: root){
            System.out.println(j.get("storeName").asText());
            Assert.assertFalse(j.get("storeName").asText().isEmpty());
        }
    }

    @Test
    public void matchingIDTest() {
        verifyStoreId("Steam",1 );
        // Name is written in all caps in the JSON file, but GoG in the assignment description
        verifyStoreId("GOG", 7);
        verifyStoreId("Fanatical", 15);
    }

    public void verifyStoreId(String storeName, int expectedStoreId) {
        for (JsonNode j : root) {
            if (storeName.equals(j.get("storeName").asText())) {
                int actualStoreId = j.get("storeID").asInt();
                System.out.println(j.get("storeName").asText() + ": " + j.get("storeID").asInt());
                Assert.assertEquals(expectedStoreId, actualStoreId);
            }
        }
    }
}
