import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class EventLogPanelTest {

    @Test
    void addEvent_shouldAppendEventToEventLog() {
        // Arrange
        EventLogPanel eventLogPanel = new EventLogPanel();
        String event = "Test event";

        // Act
        eventLogPanel.addEvent(event);

        // Assert
        JTextArea eventLogTextArea = (JTextArea) ((JViewport) ((JScrollPane) eventLogPanel.getComponent(0)).getComponent(0)).getView();
        assertEquals(event + "\n", eventLogTextArea.getText());
    }

    @Test
    void addEvent_shouldHandleMultipleEventsProperly() {
        // Arrange
        EventLogPanel eventLogPanel = new EventLogPanel();
        String firstEvent = "First event";
        String secondEvent = "Second event";

        // Act
        eventLogPanel.addEvent(firstEvent);
        eventLogPanel.addEvent(secondEvent);

        // Assert
        JTextArea eventLogTextArea = (JTextArea) ((JViewport) ((JScrollPane) eventLogPanel.getComponent(0)).getComponent(0)).getView();
        assertEquals(firstEvent + "\n" + secondEvent + "\n", eventLogTextArea.getText());
    }

    @Test
    void addEvent_shouldAppendEmptyLineForEmptyEvent() {
        // Arrange
        EventLogPanel eventLogPanel = new EventLogPanel();
        String emptyEvent = "";

        // Act
        eventLogPanel.addEvent(emptyEvent);

        // Assert
        JTextArea eventLogTextArea = (JTextArea) ((JViewport) ((JScrollPane) eventLogPanel.getComponent(0)).getComponent(0)).getView();
        assertEquals("\n", eventLogTextArea.getText());
    }
}