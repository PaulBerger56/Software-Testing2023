package Assignment3;

import Assignment1.Game;
import Assignment2.Inventory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;

public class Store {

    private Inventory inventory;
    private static final String INVENTORY_URL = "https://gist.githubusercontent.com/PaulBerger56/038362efac2d4bc044ae1e06f3aead21/raw/629b98ebe6ef4fe06eb97d390790341b7c064a1b/inventory.csv";


    public Store() throws IOException {
        inventory = new Inventory();
        loadInventoryFromFile(inventory);
    }

    public void loadInventoryFromFile(Inventory inv) throws IOException {
        FileUtils.copyURLToFile(new URL(INVENTORY_URL), new File("file.csv"));
        FileReader fr = new FileReader("file.csv");
        CSVParser parser = new CSVParser(fr, CSVFormat.DEFAULT.builder().setIgnoreSurroundingSpaces(true).setHeader().build());

        // creates the game with the ID, gets the info, and adds to the inventory
        for(CSVRecord record: parser) {
            Game game = new Game(record.get("id"));
            game.getGameInfoFromSteam();
            inventory.addGame(game);
        }
        parser.close();
        fr.close();
    }

    public Game findCheapestGame() {
        return inventory.findCheapestGame();
    }

    public Game findMostExpensiveGame() {
        return inventory.findMostExpensiveGame();
    }

    public Double getAveragePriceOfAllGames() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        inventory.printAveragePriceOfAllGames();

        return Double.parseDouble(baos.toString());
    }

    public Inventory getInventory() {
        return inventory;
    }

}
