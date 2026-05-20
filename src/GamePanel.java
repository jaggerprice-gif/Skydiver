import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements KeyListener {
    private Player player;
    private int windowWidth = 600;
    private int windowHeight = 800;

    // Constructor: initialize the game panel and player
    public GamePanel() {
        // Initialize the player: centered horizontally, positioned 3/4 down the screen
        // Player diameter is 40 pixels, so starting x is (600 - 40) / 2 = 280
        player = new Player(280, 600, 40, windowWidth);

        // Add this panel as a key listener to receive key events
        addKeyListener(this);
    }

    // paintComponent: called whenever the panel needs to be redrawn
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set background color to light blue (sky)
        setBackground(new Color(135, 206, 235));

        // Draw the player as a filled circle
        g.setColor(new Color(255, 0, 0)); // Red circle
        g.fillOval(player.getX(), player.getY(), player.getDiameter(), player.getDiameter());
    }

    // Called when a key is pressed
    public void keyPressed(KeyEvent e) {
        // Move left on left arrow key
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            player.move(-5);
            repaint();
        }
        // Move right on right arrow key
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            player.move(5);
            repaint();
        }
    }

    // Called when a key is released (not needed for this version)
    public void keyReleased(KeyEvent e) {
    }

    // Called when a key is typed (not needed for this version)
    public void keyTyped(KeyEvent e) {
    }
}
