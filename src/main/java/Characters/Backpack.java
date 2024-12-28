package Characters;

import Inventory.Treasure;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a backpack used to store treasures.
 * A backpack has a fixed capacity and allows for treasures to be added, removed,
 * or listed. It provides utility methods to manage its contents.
 */
public class Backpack {
    private static final int CAPACITY = 10;
    private int numberOfItems;
    private Map<String, Treasure> items;
    public final static String FULL_BACKPACK_MESSAGE = "Your backpack is full.";
    private final static String EMPTY_BACKPACK_MESSAGE = "Your backpack is empty.";
    private final static String NOT_IN_BACKPACK_MESSAGE = "Your backpack does not contain this treasure.";
    private final static String THROWN_FROM_BACKPACK_MESSAGE = " was thrown from your backpack.";
    final static String ADDED_TO_BACKPACK_MESSAGE = " was added to your backpack.";
    private final static String NULL_ARGUMENT_MESSAGE = "Item cannot be null.";

    private Backpack() {
        items = new HashMap<>();
    }

    public boolean isFull() {
        return numberOfItems == CAPACITY;
    }

    public boolean isEmpty() {
        return numberOfItems == 0;
    }

    public String listContents() {
        if (isEmpty()) {
            return EMPTY_BACKPACK_MESSAGE;
        }

        return items.entrySet().stream().map(item -> item.getValue().getTreasureStats())
                .collect(Collectors.joining("\n"));
    }

    public String throwItem(String item) {
        if (item != null) {
            item = item.toLowerCase();
            if (isEmpty()) {
                return EMPTY_BACKPACK_MESSAGE;
            }
            String finalItem = item;
            return items.values().stream()
                    .anyMatch(t -> t.getName().toLowerCase().equals(finalItem))
                    ? item + THROWN_FROM_BACKPACK_MESSAGE 
                    : NOT_IN_BACKPACK_MESSAGE;
        }

        throw new IllegalArgumentException(NULL_ARGUMENT_MESSAGE);
    }

    public Treasure throwRandomTreasure() {
        if (!isEmpty()) {
            return items.entrySet().stream().findAny().get().getValue();
        }

        return null;
    }

    public String addItem(Treasure item) {
        if (item != null) {
            if (isFull()) {
                return FULL_BACKPACK_MESSAGE;
            }

            if (items.put(item.getName(), item) == null) {
                numberOfItems++;
                return item.getTreasureStats() + ADDED_TO_BACKPACK_MESSAGE;
            }

        }

        throw new IllegalArgumentException(NULL_ARGUMENT_MESSAGE);
    }

    public Treasure getItem(String name) {
        name = name.toLowerCase();
        String finalName = name;
        Treasure item = items.values().stream()
                .filter(t -> t.getName().toLowerCase().equals(finalName))
                .findFirst()
                .orElse(null);
        if (item != null) {
            items.remove(item.getName());
            numberOfItems--;
        }

        return item;

    }

    public static Backpack getBackpackInstance() {
        return new Backpack();
    }
}
