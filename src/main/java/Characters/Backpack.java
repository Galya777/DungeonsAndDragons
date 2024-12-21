package Characters;

import Inventory.Treasure;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
            if (isEmpty()) {
                return EMPTY_BACKPACK_MESSAGE;
            }

            return (items.get(item) != null) ? item + THROWN_FROM_BACKPACK_MESSAGE : NOT_IN_BACKPACK_MESSAGE;

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
        if (name == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT_MESSAGE);
        }

        Treasure item = items.remove(name);
        if (item != null) {
            numberOfItems--;
        }

        return item;

    }

    public static Backpack getBackpackInstance() {
        return new Backpack();
    }
}
