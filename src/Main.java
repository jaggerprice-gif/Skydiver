import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Skydiving Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 800);
        frame.add(new GamePanel());

        // Prevent window from being resized
        frame.setResizable(false);

        frame.setVisible(true);
    }
}
