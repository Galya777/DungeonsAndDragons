package Characters;

/**
 * Represents a position in a two-dimensional grid with row and column indices.
 * Provides methods to get and set the row and column values as well as
 * utilities for object comparison and string representation.
 */
public class Position {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Position(Position other) {
        this.row = other.row;
        this.col = other.col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean setRow(int row) {
        if (row > -1) {
            this.row = row;
            return true;
        }
        return false;

    }

    public boolean setCol(int col) {
        if (col > -1) {
            this.col = col;
            return true;
        }
        return false;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + col;
        result = prime * result + row;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Position other = (Position) obj;
        if (col != other.col)
            return false;
        if (row != other.row)
            return false;
        return true;
    }

    public static Position createNewPosition(int row, int col) {
        return new Position(row, col);
    }

    @Override
public String toString() {
    return row + "," + col;
}
}

