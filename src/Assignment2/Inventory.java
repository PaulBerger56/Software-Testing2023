package Assignment2;

import Assignment1.Game;

import java.util.ArrayList;

public class Inventory {

    private final ArrayList<Game> games;

    public Inventory() {
        games = new ArrayList<>();
    }

    /**
     *  Checks for duplicates and only adds if duplicate doesn't exist.
     */
    public void addGame(Game game) {
        for (int i = 0; i < games.size(); i++) {
            if(games.get(i).getSteamID().equals(game.getSteamID())) {
                return;
            }
        }
        games.add(game);
    }

    public void remove(Game game) {
        for (int i = 0; i < games.size(); i++) {
            if(games.get(i).getSteamID().equals(game.getSteamID())) {
                games.remove(i);
            }
        }
    }

    public Game findCheapestGame() {
        if(games.isEmpty()) {
            return null;
        }

        Game tempGame = games.get(0);
        for(Game g: games) {
            if (g.getPrice() < tempGame.getPrice()) {
                tempGame = g;
            }
        }
        return tempGame;
    }

    public Game findMostExpensiveGame() {
        if(games.isEmpty()) {
            return null;
        }

        Game tempGame = games.get(0);
        for(Game g: games) {
            if (g.getPrice() > tempGame.getPrice()) {
                tempGame = g;
            }
        }
        return tempGame;
    }

    public Game findMostHighlyRatedGame() {
        if(games.isEmpty()) {
            return null;
        }

        Game tempGame = games.get(0);
        for(Game g: games) {
            if(Double.parseDouble(g.getRating()) >  Double.parseDouble(tempGame.getRating())) {
                tempGame = g;
            }
        }
        return tempGame;
    }

    public void printAveragePriceOfAllGames() {
        double total = 0.0;

        for(Game g: games) {
            total += g.getPrice();
        }

        total /= games.size();
        System.out.println(total);
    }

    public void printGameData() {
        for(Game g: games) {
            g.printGame();
            System.out.println();
        }
    }

    public int getSize() {
        return games.size();
    }

    public ArrayList<Game> getGames() {
        return games;
    }
}
