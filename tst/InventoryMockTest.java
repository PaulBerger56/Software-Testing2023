import Assignment1.Game;
import Assignment2.Inventory;
import Assignment7.InventoryInterface;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.SQLOutput;

public class InventoryMockTest {

    @Mock
    private InventoryInterface invIF;

    @Test
    public void testCheapestGame() {
        InventoryInterface invMock = Mockito.mock();
        Mockito.when(invMock.findCheapestGame()).thenReturn(
                new Game("12345", "PC", "Madden NFL", "01/01/2023", "EA Games", "Sports", 60, "5.0"));

        Assert.assertEquals("Madden NFL", invMock.findCheapestGame().getName());
    }

    @Test
    public void testInventoryInterface() {
        InventoryInterface invMock = Mockito.mock();
        Mockito.when(invMock.findCheapestGame()).thenReturn(
                new Game("12345", "PC", "Madden NFL", "01/01/2023", "EA Games", "Sports", 60, "5.0"));
        System.out.println("Madden NFL");
        Assert.assertEquals("Madden NFL", invMock.findCheapestGame().getName());
    }

    @Test
    public void marioTester() {
        InventoryInterface invMock = Mockito.mock();
        Mockito.when(invMock.findMostExpensiveGame()).thenReturn(
                new Game("86462", "Super Nintendo", "Super Mario Bros", "01/01/93", "Nintendo", "Platformer", 30, "5.0"));

        System.out.println(invMock.findMostExpensiveGame().getName());
        Assert.assertEquals("Super Mario Bros", invMock.findMostExpensiveGame().getName());
    }

    @Test
    public void testAveragePrice() {
        InventoryInterface invMock = Mockito.mock();
        Mockito.when(invMock.getAveragePriceOfAllGames()).thenReturn(60.0);

        System.out.println(invMock.getAveragePriceOfAllGames());
        Assert.assertEquals(60.0, invMock.getAveragePriceOfAllGames(), 0);
    }
}
