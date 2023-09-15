import Assignment1.Game;
import Assignment2.Inventory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class InventoryTest {

    private Inventory games = new Inventory();
    // Elden Ring, Fallout 4, Resident Evil 2
    private String[] steamIDs = {"883710", "1245620", "377160"};

    @Before
    public void setUp() {
        for (int i = 0; i < steamIDs.length; i++) {
            String steamID = steamIDs[i];
            Game game = new Game(steamID);
            game.getGameInfoFromSteam();
            games.addGame(game);
        }
    }


    @Test
    public void testAddingToInventory() {
        int size = games.getSize();
        Game game = new Game("894020"); // Game's name is Death's Door
        game.getGameInfoFromSteam();
        games.addGame(game);
        Assert.assertEquals("When you add one game, the inventory increases by one", size+1, games.getSize());
        // Let's test adding a duplicate game
        Game secondGame = new Game("883710");
        secondGame.getGameInfoFromSteam();
        games.addGame(secondGame);
        Assert.assertEquals("Duplicate Game added", size+1, games.getSize());
    }

    @Test
    public void testRemoveGameFromInventory() {
        int size = games.getSize();
        Game game = new Game("883710");
        game.getGameInfoFromSteam();
        games.remove(game);
        Assert.assertEquals("", size-1, games.getSize());
    }

    @Test
    public void testCheapestGame() {
        // Cheapest in the inventory is fallout 4, 1999 cents
        games.printGameData();
        Game cheapest = games.findCheapestGame();
        Assert.assertEquals("Sometimes fails when the api sends the wrong amounts",1999.0, cheapest.getPrice(),0);
    }

    @Test
    public void testHighestRatedGame() {
        //Elden Ring 94, Fallout 4 84, Resident Evil 2 89
        Game highestRated = games.findMostHighlyRatedGame();
        Assert.assertEquals(94, Integer.parseInt(highestRated.getRating()));
    }

    @Test
    public void testPrintAveragePriceOfAllGames() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        games.printAveragePriceOfAllGames();

        double testPrice = 0.0;
        for(Game g: games.getGames()) {
            testPrice += g.getPrice();
        }
        Assert.assertEquals(testPrice/games.getSize(), Double.parseDouble(baos.toString()),10);
    }
}
