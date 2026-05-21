import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener {
    private Player player;
    private Obstacle obstacle;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private Timer gameTimer;

    // Score tracking fields
    private int score = 0;
    private int highScore = 0;
    private boolean gameOver = false;
    private boolean scored = false;

    // Replay button for Game Over screen
    private JButton replayButton;

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.CYAN);

        player = new Player(180, 200, 40, 400);
        obstacle = new Obstacle(400);

        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (movingLeft) {
                    player.move(-5);
                }
                if (movingRight) {
                    player.move(5);
                }

                obstacle.move();

                if (obstacle.getY() + obstacle.getSize() < player.getY() && !scored) {
                    score += 10;
                    scored = true;
                }

                if (obstacle.isOffScreen()) {
                    obstacle.respawn();
                    scored = false;
                }

                if (collides()) {
                    if (score > highScore) {
                        highScore = score;
                    }
                    gameOver = true;
                    replayButton.setVisible(true);
                    gameTimer.stop();
                }

                repaint();
            }
        });
        gameTimer.start();

        // Create replay button for Game Over screen
        replayButton = new JButton("Play Again");
        replayButton.setFont(new Font("Arial", Font.BOLD, 20));
        replayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                score = 0;
                gameOver = false;
                scored = false;
                player = new Player(180, 200, 40, 400);
                obstacle.respawn();
                replayButton.setVisible(false);
                gameTimer.start();
                requestFocusInWindow();
            }
        });
        replayButton.setVisible(false);
        this.add(replayButton);
    }

    private boolean collides() {
        int playerX = player.getX();
        int playerY = player.getY();
        int playerSize = player.getDiameter();

        int obstacleX = obstacle.getX();
        int obstacleY = obstacle.getY();
        int obstacleSize = obstacle.getSize();

        return playerX < obstacleX + obstacleSize &&
                playerX + playerSize > obstacleX &&
                playerY < obstacleY + obstacleSize &&
                playerY + playerSize > obstacleY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            Font gameOverFont = new Font("Arial", Font.BOLD, 48);
            g.setFont(gameOverFont);
            String gameOverText = "GAME OVER";
            FontMetrics fm = g.getFontMetrics();
            int gameOverX = (getWidth() - fm.stringWidth(gameOverText)) / 2;
            g.drawString(gameOverText, gameOverX, 300);

            Font scoreFont = new Font("Arial", Font.BOLD, 28);
            g.setFont(scoreFont);
            String scoreText = "Score: " + score;
            fm = g.getFontMetrics();
            int scoreX = (getWidth() - fm.stringWidth(scoreText)) / 2;
            g.drawString(scoreText, scoreX, 370);

            String highScoreText = "High Score: " + highScore;
            fm = g.getFontMetrics();
            int highScoreX = (getWidth() - fm.stringWidth(highScoreText)) / 2;
            g.drawString(highScoreText, highScoreX, 420);
        } else {
            g.setColor(Color.RED);
            g.fillOval(player.getX(), player.getY(), player.getDiameter(), player.getDiameter());

            g.setColor(Color.GRAY);
            g.fillRect(obstacle.getX(), obstacle.getY(), obstacle.getSize(), obstacle.getSize());

            g.setColor(Color.WHITE);
            Font scoreFont = new Font("Arial", Font.BOLD, 20);
            g.setFont(scoreFont);
            g.drawString("Score: " + score, 10, 25);
        }
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