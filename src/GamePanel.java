import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener {
    private Player player;
    private Obstacle obstacle;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private Timer gameTimer;

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.CYAN);

        player = new Player(180, 200, 40, 400);
        // Initialize obstacle with window width
        obstacle = new Obstacle(400);

        // Use gameTimer as a class field so it can be stopped on collision
        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update player position
                if (movingLeft) {
                    player.move(-5);
                }
                if (movingRight) {
                    player.move(5);
                }

                // Update obstacle position
                obstacle.move();

                // Check if obstacle has left the screen, and respawn if so
                if (obstacle.isOffScreen()) {
                    obstacle.respawn();
                }

                // Check for collision and stop game if collision detected
                if (collides()) {
                    gameTimer.stop();
                }

                repaint();
            }
        });
        gameTimer.start();
    }

    // Collision detection: checks if player rectangle and obstacle rectangle overlap
    private boolean collides() {
        int playerX = player.getX();
        int playerY = player.getY();
        int playerSize = player.getDiameter();

        int obstacleX = obstacle.getX();
        int obstacleY = obstacle.getY();
        int obstacleSize = obstacle.getSize();

        // Simple bounding box overlap: two rectangles collide if they overlap on both axes
        return playerX < obstacleX + obstacleSize &&
               playerX + playerSize > obstacleX &&
               playerY < obstacleY + obstacleSize &&
               playerY + playerSize > obstacleY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw player as red circle
        g.setColor(Color.RED);
        g.fillOval(player.getX(), player.getY(), player.getDiameter(), player.getDiameter());

        // Draw obstacle as grey square
        g.setColor(Color.GRAY);
        g.fillRect(obstacle.getX(), obstacle.getY(), obstacle.getSize(), obstacle.getSize());
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
    public void keyTyped(KeyEvent e) {}
}
