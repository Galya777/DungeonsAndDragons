package Characters;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

