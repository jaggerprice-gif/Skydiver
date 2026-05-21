import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener {
    private Player player;

    // Booleans to track key states for smooth movement
    private boolean movingLeft = false;
    private boolean movingRight = false;

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.CYAN);

        // Initialize player at starting position (y = 200, which is 1/4 from top of 800px window)
        player = new Player(180, 200, 40, 400);

        // Timer for smooth movement at ~60fps (fires every 16ms)
        // Uses anonymous ActionListener class for AP CSA compatibility
        Timer gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check movement booleans and update player position
                if (movingLeft) {
                    player.move(-5);
                }
                if (movingRight) {
                    player.move(5);
                }
                repaint();
            }
        });
        gameTimer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillOval(player.getX(), player.getY(), player.getDiameter(), player.getDiameter());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            movingLeft = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            movingRight = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            movingLeft = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            movingRight = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}
