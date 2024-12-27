package Characters;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PositionTest {

    /**
     * Tests for the setRow method of the Position class.
     * The setRow method sets the row coordinate of the Position object
     * if the provided value is greater than -1 and returns true.
     * Otherwise, it doesn't change the value and returns false.
     */

    @Test
    public void testSetRow_PositiveValue() {
        Position position = new Position(0, 0);
        boolean result = position.setRow(5);

        assertTrue(result, "setRow should return true for positive row values.");
        assertEquals(5, position.getRow(), "The row value should be updated to 5.");
    }

    @Test
    public void testSetRow_ZeroValue() {
        Position position = new Position(3, 3);
        boolean result = position.setRow(0);

        assertTrue(result, "setRow should return true for a row value of 0.");
        assertEquals(0, position.getRow(), "The row value should be updated to 0.");
    }

    @Test
    public void testSetRow_NegativeValue() {
        Position position = new Position(7, 7);
        boolean result = position.setRow(-1);

        assertFalse(result, "setRow should return false for negative row values.");
        assertEquals(7, position.getRow(), "The row value should remain unchanged.");
    }

    @Test
    public void testSetRow_NoChangeOnInvalidValue() {
        Position position = new Position(2, 2);
        position.setRow(-5);

        assertEquals(2, position.getRow(), "The row value should not be updated when setRow is called with a negative value.");
    }
}