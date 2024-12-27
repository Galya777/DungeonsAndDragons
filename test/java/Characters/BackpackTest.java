package Characters;

import Inventory.Treasure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BackpackTest {

    @Test
    void testAddItemSuccessfully() {
        // Arrange
        Backpack backpack = Backpack.getBackpackInstance();
        Treasure mockTreasure = mock(Treasure.class);

        when(mockTreasure.getName()).thenReturn("Golden Coin");
        when(mockTreasure.getTreasureStats()).thenReturn("Golden Coin - Shiny and valuable");

        // Act
        String result = backpack.addItem(mockTreasure);

        // Assert
        assertEquals("Golden Coin - Shiny and valuable was added to your backpack.", result);
    }

    @Test
    void testAddItemBackpackFull() {
        // Arrange
        Backpack backpack = Backpack.getBackpackInstance();
        Treasure mockTreasure = mock(Treasure.class);
        when(mockTreasure.getName()).thenReturn("Golden Coin");
        when(mockTreasure.getTreasureStats()).thenReturn("Golden Coin - Shiny and valuable");

        for (int i = 0; i < 10; i++) {
            Treasure tempTreasure = mock(Treasure.class);
            when(tempTreasure.getName()).thenReturn("Item" + i);
            backpack.addItem(tempTreasure);
        }

        // Act
        String result = backpack.addItem(mockTreasure);

        // Assert
        assertEquals(Backpack.FULL_BACKPACK_MESSAGE, result);
    }

    @Test
    void testAddItemNullTreasure() {
        // Arrange
        Backpack backpack = Backpack.getBackpackInstance();

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> backpack.addItem(null));
        assertEquals("Item cannot be null.", exception.getMessage());
    }

    @Test
    void testAddDuplicateItem() {
        // Arrange
        Backpack backpack = Backpack.getBackpackInstance();
        Treasure mockTreasure = mock(Treasure.class);

        when(mockTreasure.getName()).thenReturn("Golden Coin");
        when(mockTreasure.getTreasureStats()).thenReturn("Golden Coin - Shiny and valuable");

        backpack.addItem(mockTreasure);

        // Act
        String result = backpack.addItem(mockTreasure);

        // Assert
        assertEquals("Golden Coin - Shiny and valuable was added to your backpack.", result);
    }
}