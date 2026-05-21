import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements KeyListener {
    private Player player;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Obstacle> scoredObstacles;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private Timer gameTimer;

    private int score = 0;
    private int highScore = 0;
    private boolean gameOver = false;

    // Spawn timer manages when new obstacles are added to the game
    private int spawnTimer = 0;
    private final int SPAWN_INTERVAL = 120; // New obstacle spawns every ~2 seconds (120 ticks at 60 FPS)

    private JButton replayButton;

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.CYAN);

        player = new Player(180, 200, 40, 400);
        obstacles = new ArrayList<Obstacle>();
        scoredObstacles = new ArrayList<Obstacle>();
        // Add first obstacle so screen isn't empty at start
        obstacles.add(new Obstacle(400));

        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (movingLeft) {
                    player.move(-5);
                }
                if (movingRight) {
                    player.move(5);
                }

                // Increment spawn timer and add new obstacle at regular intervals
                spawnTimer++;
                if (spawnTimer >= SPAWN_INTERVAL) {
                    obstacles.add(new Obstacle(400));
                    spawnTimer = 0;
                }

                // Process all obstacles (iterate backwards for safe removal)
                for (int i = obstacles.size() - 1; i >= 0; i--) {
                    Obstacle obs = obstacles.get(i);
                    obs.move();

                    // Award points when obstacle passes player (without re-scoring same obstacle)
                    if (obs.getY() + obs.getSize() < player.getY() && !scoredObstacles.contains(obs)) {
                        score += 10;
                        scoredObstacles.add(obs);
                    }

                    // Remove obstacle if it goes off screen
                    if (obs.isOffScreen()) {
                        obstacles.remove(i);
                        scoredObstacles.remove(obs);
                    } else if (collides(obs)) {
                        // Check collision with remaining obstacles
                        if (score > highScore) {
                            highScore = score;
                        }
                        gameOver = true;
                        replayButton.setVisible(true);
                        gameTimer.stop();
                    }
                }

                repaint();
            }
        });
        gameTimer.start();

        replayButton = new JButton("Play Again");
        replayButton.setFont(new Font("Arial", Font.BOLD, 20));
        replayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                score = 0;
                gameOver = false;
                player = new Player(180, 200, 40, 400);
                obstacles.clear();
                scoredObstacles.clear();
                obstacles.add(new Obstacle(400));
                spawnTimer = 0;
                replayButton.setVisible(false);
                gameTimer.start();
                requestFocusInWindow();
            }
        });
        replayButton.setVisible(false);
        this.add(replayButton);
    }

    private boolean collides(Obstacle obs) {
        int playerX = player.getX();
        int playerY = player.getY();
        int playerSize = player.getDiameter();

        int obstacleX = obs.getX();
        int obstacleY = obs.getY();
        int obstacleSize = obs.getSize();

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

            // Draw all obstacles in the ArrayList
            g.setColor(Color.GRAY);
            for (int i = 0; i < obstacles.size(); i++) {
                Obstacle obs = obstacles.get(i);
                g.fillRect(obs.getX(), obs.getY(), obs.getSize(), obs.getSize());
            }

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