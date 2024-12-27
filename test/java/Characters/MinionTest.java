package Characters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MinionTest {

    /**
     * Tests for the revive method in the Minion class.
     * <p>
     * The revive method is responsible for modifying the Minion's stats based on their level
     * and setting a new Position for the Minion. The position and stats should align with the input and logic inside this method.
     */

    @Test
    void revive_shouldUpdatePosition() {
        // Arrange
        Position initialPosition = new Position(1, 1);
        Position newPosition = new Position(2, 2);
        Stats statsMock = mock(Stats.class);
        Minion minion = spy(new Minion("Minion Name", "ID001", initialPosition, "image/path"));
        minion.stats = statsMock;

        // Act
        minion.revive(newPosition);

        // Assert
        assertEquals(newPosition, minion.getPosition(), "Minion's position should update to the new position after revive.");
    }

    @Test
    void revive_shouldModifyStatsByLevel() {
        // Arrange
        Position position = new Position(1, 1);
        Stats statsMock = mock(Stats.class);
        Minion minion = spy(new Minion("Minion Name", "ID001", position, "image/path"));
        int level = 15; // We control the level here for consistency
       // doReturn(level).when(minion).getLevel();
        minion.stats = statsMock;

        // Act
        minion.revive(new Position(3, 4));

        // Assert
        verify(statsMock).modifyByLevel(level);
    }

    @Test
    void revive_shouldNotThrowOnNullPosition() {
        // Arrange
        Position initialPosition = new Position(1, 1);
        Stats statsMock = mock(Stats.class);
        Minion minion = spy(new Minion("Minion Name", "ID001", initialPosition, "image/path"));
        minion.stats = statsMock;

        // Act & Assert
        assertDoesNotThrow(() -> minion.revive(null), "The revive method should not throw if the new position is null.");
    }

    @Test
    void revive_shouldHandleIdenticalPositionsCorrectly() {
        // Arrange
        Position position = new Position(5, 5);
        Stats statsMock = mock(Stats.class);
        Minion minion = spy(new Minion("Minion Name", "ID001", position, "image/path"));
        minion.stats = statsMock;

        // Act
        minion.revive(position);

        // Assert
        assertEquals(position, minion.getPosition(), "The position should remain correct if the same position is revived.");
        verify(statsMock).modifyByLevel(anyInt());
    }
}