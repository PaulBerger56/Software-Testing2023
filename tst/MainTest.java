import Assignment1.Game;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MainTest {

    // from https://steamdb.info/
    // pick at least five AppID of your favorite games
    private static String[] GAME_IDS = {"594650", "252490", "1235140", "49520", "413150"};

    @Test
    public void testSteamGames() {

        String url = "https://store.steampowered.com/api/appdetails?appids=";
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Game> games = new ArrayList();

        try {
            for (int i = 0; i < GAME_IDS.length; i++) {
                JsonNode root =  mapper.readTree(new URL(url + GAME_IDS[i]));
                String name = root.get(GAME_IDS[i]).get("data").get("name").asText();
                String platform = root.get(GAME_IDS[i]).get("data").get("platforms").get("windows").asText();
                String score = root.get(GAME_IDS[i]).path("data").path("metacritic").path("score").asText();
                String developer = root.get(GAME_IDS[i]).get("data").get("developers").get(0).asText();
                String genre = root.get(GAME_IDS[i]).get("data").get("genres").get(0).get("description").asText();
                String releaseDate = root.get(GAME_IDS[i]).get("data").get("release_date").get("date").asText();
                String price = root.get(GAME_IDS[i]).get("data").get("price_overview").get("final").asText();

                //instantiate game object and print out
              // games.add(new Game("PC", name, releaseDate, developer, genre, price, score));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Game g: games) {
            g.printGame();
            System.out.println();
        }
    }

    @Test
    public void testGame() {
        Game[] games = new Game[3];
        // download json file of games and create objects based on it
        String url = "https://www.cheapshark.com/api/1.0/deals?storeID=1";
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(new URL(url));

            for (int i = 0; i < root.size(); i++) {
                String title = root.get(i).get("title").asText();
                double salePrice = root.get(i).get("salePrice").asDouble();
                int criticScore = root.get(i).get("metacriticScore").asInt();
                System.out.println(title + "; " + salePrice + "; " + criticScore);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
