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

    private int spawnTimer = 0;
    private int spawnInterval = 60;

    // Progressive difficulty variables
    private int obstacleSpeed = 5;           // Starting obstacle speed
    private int lastDifficultyScore = 0;     // Tracks score at last difficulty increase

    private JButton replayButton;

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.CYAN);

        player = new Player(180, 400, 40, 400);
        obstacles = new ArrayList<Obstacle>();
        scoredObstacles = new ArrayList<Obstacle>();
        obstacles.add(new Obstacle(400, obstacleSpeed));

        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (movingLeft) {
                    player.move(-5);
                }
                if (movingRight) {
                    player.move(5);
                }

                spawnTimer++;
                if (spawnTimer >= spawnInterval) {
                    obstacles.add(new Obstacle(400, obstacleSpeed));
                    spawnTimer = 0;
                }

                for (int i = obstacles.size() - 1; i >= 0; i--) {
                    Obstacle obs = obstacles.get(i);
                    obs.move();

                    if (obs.getY() + obs.getSize() < player.getY() && !scoredObstacles.contains(obs)) {
                        score += 10;
                        scoredObstacles.add(obs);
                    }

                    if (obs.isOffScreen()) {
                        obstacles.remove(i);
                        scoredObstacles.remove(obs);
                    } else if (collides(obs)) {
                        if (score > highScore) {
                            highScore = score;
                        }
                        gameOver = true;
                        replayButton.setVisible(true);
                        gameTimer.stop();
                    }
                }

                // Difficulty scaling: increase every 50 points
                if (score >= lastDifficultyScore + 50) {
                    lastDifficultyScore = score;
                    // Decrease spawn interval for more frequent obstacles (minimum 10)
                    if (spawnInterval > 10) {
                        spawnInterval -= 10;
                    }
                    // Increase obstacle speed for faster movement (maximum 8)
                    if (obstacleSpeed < 8) {
                        obstacleSpeed++;
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
                player = new Player(180, 400, 40, 400);
                obstacles.clear();
                scoredObstacles.clear();
                obstacles.add(new Obstacle(400, obstacleSpeed));
                spawnTimer = 0;
                // Reset difficulty scaling variables
                obstacleSpeed = 5;
                spawnInterval = 60;
                lastDifficultyScore = 0;
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