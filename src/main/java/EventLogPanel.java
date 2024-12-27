import javax.swing.*;
import java.awt.*;

/**
 * EventLogPanel is a custom JPanel used to display a log of events.
 * It provides a read-only text area within a scrollable pane for listing events.
 * Events can be added dynamically using the addEvent method.
 */
public class EventLogPanel extends JPanel {
    private JTextArea eventLog;

    public EventLogPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Event Log"));

        eventLog = new JTextArea(5, 50);
        eventLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(eventLog);

        add(scrollPane, BorderLayout.CENTER);
    }

    // Add event to the log
    public void addEvent(String event) {
        eventLog.append(event + "\n");
    }
}
