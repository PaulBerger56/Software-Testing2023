package Assignment1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

public class Game {

    private String steamID;
    private String platform;
    private String name;
    private String date;
    private String developer;
    private String genre;
    private double price;
    private String rating;


    public Game(String steamID) {
        this.steamID = steamID;
    }

    public Game(String steamID, String platform, String name, String date, String developer, String genre, double price, String rating) {
        this.steamID = steamID;
        this.platform = platform;
        this.name = name;
        this.date = date;
        this.developer = developer;
        this.genre = genre;
        this.price = price;
        this.rating = rating;
    }

    public void getGameInfoFromSteam() {
        String url = "https://store.steampowered.com/api/appdetails?appids=";
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(new URL(url + steamID));
            String name = root.get(steamID).get("data").get("name").asText();
            String platform = root.get(steamID).get("data").get("platforms").get("windows").asText();
            int score = root.get(steamID).path("data").path("metacritic").path("score").asInt();
            String developer = root.get(steamID).get("data").get("developers").get(0).asText();
            String genre = root.get(steamID).get("data").get("genres").get(0).get("description").asText();
            String releaseDate = root.get(steamID).get("data").get("release_date").get("date").asText();
            double price = root.get(steamID).get("data").get("price_overview").get("final").asDouble();
            this.name = name;
            this.platform = "PC";
            this.rating = String.valueOf(score);
            this.developer = developer;
            this.genre = genre;
            this.date = releaseDate;
            this.price = price;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public String getSteamID() {
        return steamID;
    }

    public void setSteamID(String steamID) {
        this.steamID = steamID;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void printGame() {
        System.out.println(steamID);
        System.out.println(platform);
        System.out.println(name);
        System.out.println(date);
        System.out.println(developer);
        System.out.println(genre);
        System.out.println(price);
        System.out.println(rating);
    }
}
