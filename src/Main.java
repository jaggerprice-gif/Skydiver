import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // Create a new JFrame window
        JFrame frame = new JFrame("Skydiving Game - Version 1");

        // Set the window size
        frame.setSize(600, 800);

        // Close the program when the window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the game panel and add it to the frame
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);

        // Set focus to the game panel so it receives key events
        gamePanel.setFocusable(true);

        // Display the window
        frame.setVisible(true);
    }
}
