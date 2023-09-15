import Assignment3.Store;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class StoreTest {
    private static final String INVENTORY_URL = "https://gist.githubusercontent.com/PaulBerger56/038362efac2d4bc044ae1e06f3aead21/raw/629b98ebe6ef4fe06eb97d390790341b7c064a1b/inventory.csv";
    private static Store store;

    @Before
    public void setup() throws IOException {
        store = new Store();
    }

    @Test
    public void testDownload() throws IOException {
       String str = IOUtils.toString(new URL(INVENTORY_URL).openStream(), "UTF-8");
       String[] lines = str.split("\n");
       ArrayList<String> ids = new ArrayList<>();
        for(int i = 1; i < lines.length; i++) {
            String[] brokenLine = lines[i].split(",");
            ids.add(brokenLine[0]);
        }
        for (String i: ids) {
            System.out.println(i);
        }
        Assert.assertEquals(4, ids.size());
    }

    @Test
    public void testLoadInventoryFromWeb() throws Exception {
        Assert.assertEquals(4, store.getInventory().getSize());
        store.getInventory().printGameData();
    }

    @Test
    public void testFindCheapestGame() {
        Assert.assertEquals("Should be Portal 2, 620, unless there is a sale or issue with the data",
                "620", store.findCheapestGame().getSteamID());
    }

    @Test
    public void testFindMostExpensiveGame() {
        Assert.assertEquals("Should be Baldur's gate 3, 1086940, unless there is a sale or issue with the data",
                "1086940", store.findMostExpensiveGame().getSteamID());
    }

    @Test
    public void testGetAveragePriceOfAllGames() {
        System.out.println(store.getAveragePriceOfAllGames());
        Assert.assertEquals("Should work unless there is an issue with the data", 3249.0, store.getAveragePriceOfAllGames(),0);
    }
}
