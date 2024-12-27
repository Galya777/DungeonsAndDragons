package Characters;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GamePanel is a custom JPanel subclass that serves as a dedicated panel
 * for rendering game objects. It manages and visually represents a collection
 * of game objects drawn dynamically on the panel.
 *
 * Responsibilities:
 * - Maintains a list of game objects to be displayed.
 * - Provides a method to add new game objects to the panel.
 * - Overrides the paintComponent method to draw all the game objects.
 *
 * Usage Scenarios:
 * - Suitable for 2D games where characters or objects need to be
 *   dynamically drawn on the screen.
 *
 * Components:
 * - A collection of game objects represented by instances of classes
 *   implementing the `Character` interface.
 *
 * Extends:
 * - javax.swing.JPanel
 *
 * Thread Safety:
 * - Not guaranteed to be thread-safe. Synchronization is required
 *   when modifying the gameObjects list from multiple threads.
 */
public class GamePanel extends JPanel {
    private List<Character> gameObjects = new ArrayList<>();

    public void addGameObject(Character obj) {
        gameObjects.add(obj);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear previous drawings
        for (Character obj : gameObjects) {
            obj.draw(g); // Draw each game object
        }
    }
}

