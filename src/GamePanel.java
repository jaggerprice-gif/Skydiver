import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
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
    private int obstacleSpeed = 5;
    private int tickCount = 0;
    private static final int DIFFICULTY_INTERVAL = 600;

    private Font starCrushFont;
    private ImageIcon playIcon;
    private ImageIcon homeIcon;
    private ImageIcon replayIcon;
    private BufferedImage bgImage;
    private int bgY = 0;
    private BufferedImage startBgImage;
    private static final int BG_DRAW_WIDTH = 400;
    private static final int BG_DRAW_HEIGHT = 8000;
    private static final int CLOUD_HEIGHT = 80;
    private BufferedImage[] cloudImages = new BufferedImage[5];
    private int nextCloudIndex = 0;

    // Customization screen
    private boolean onCustomizeScreen = false;
    private Color parachuteColor = Color.WHITE;
    private BufferedImage parachuteOutline;
    private BufferedImage parachuteFilling;
    private ImageIcon customizeIcon;
    private JButton customizeButton;
    private JButton backFromCustomizeButton;
    private JButton[] swatchButtons = new JButton[8];
    private int selectedSwatchIndex = 0;

    // Cat selection
    private BufferedImage[] catImages = new BufferedImage[6];
    private int selectedCatIndex = 0;
    private JButton[] catButtons = new JButton[6];

    // Player draw size (separate from hitbox diameter)
    private static final int PLAYER_DRAW_SIZE = 75;
    private static final int CAT_DRAW_SIZE = 34;

    private static final Color[] SWATCH_COLORS = {
            Color.WHITE,
            Color.getHSBColor(0f,    0.4f, 1f),
            Color.getHSBColor(0.08f, 0.4f, 1f),
            Color.getHSBColor(0.17f, 0.4f, 1f),
            Color.getHSBColor(0.33f, 0.4f, 1f),
            Color.getHSBColor(0.58f, 0.4f, 1f),
            Color.getHSBColor(0.69f, 0.4f, 1f),
            Color.getHSBColor(0.79f, 0.4f, 1f)
    };

    private JButton replayButton;
    private boolean onHomeScreen = true;
    private JButton playButton;
    private JButton homeButton;

    public GamePanel() {
        try {
            starCrushFont = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/fonts/Star Crush.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(starCrushFont);
        } catch (FontFormatException e) {
            starCrushFont = new Font("Arial", Font.PLAIN, 12);
        } catch (IOException e) {
            starCrushFont = new Font("Arial", Font.PLAIN, 12);
        }

        try {
            BufferedImage playImg = ImageIO.read(getClass().getResourceAsStream("/images/playbutton.png"));
            BufferedImage homeImg = ImageIO.read(getClass().getResourceAsStream("/images/homebutton.png"));
            playIcon = new ImageIcon(playImg.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
            homeIcon = new ImageIcon(homeImg.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
            BufferedImage replayImg = ImageIO.read(getClass().getResourceAsStream("/images/replaybutton.png"));
            replayIcon = new ImageIcon(replayImg.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
            bgImage = ImageIO.read(getClass().getResourceAsStream("/images/skybackground.png"));
            startBgImage = ImageIO.read(getClass().getResourceAsStream("/images/startbackground.png"));
            for (int i = 0; i < 5; i++) {
                cloudImages[i] = ImageIO.read(getClass().getResourceAsStream("/images/cloud" + (i + 1) + "new.png"));
            }
            parachuteOutline = ImageIO.read(getClass().getResourceAsStream("/images/parachuteoutline.png"));
            parachuteFilling = ImageIO.read(getClass().getResourceAsStream("/images/parachutefilling.png"));
            BufferedImage customizeImg = ImageIO.read(getClass().getResourceAsStream("/images/customizebutton.png"));
            customizeIcon = new ImageIcon(customizeImg.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
            for (int i = 0; i < 6; i++) {
                catImages[i] = ImageIO.read(getClass().getResourceAsStream("/images/cat" + (i + 1) + ".png"));
            }
        } catch (IOException e) {
            playIcon = null;
            homeIcon = null;
            replayIcon = null;
        }

        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.BLACK);
        setLayout(null);

        player = new Player(180, 400, 45, 360, parachuteColor, selectedCatIndex);
        obstacles = new ArrayList<Obstacle>();
        scoredObstacles = new ArrayList<Obstacle>();
        int hbW = 40, hbH = 40;
        if (cloudImages[nextCloudIndex] != null) {
            hbH = CLOUD_HEIGHT;
            hbW = cloudImages[nextCloudIndex].getWidth() * CLOUD_HEIGHT / cloudImages[nextCloudIndex].getHeight();
        }
        obstacles.add(new Obstacle(400, obstacleSpeed, nextCloudIndex, hbW, hbH));
        nextCloudIndex = (nextCloudIndex + 1) % 5;

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
                    int hbW = 40, hbH = 40;
                    if (cloudImages[nextCloudIndex] != null) {
                        hbH = CLOUD_HEIGHT;
                        hbW = cloudImages[nextCloudIndex].getWidth() * CLOUD_HEIGHT / cloudImages[nextCloudIndex].getHeight();
                    }
                    obstacles.add(new Obstacle(400, obstacleSpeed, nextCloudIndex, hbW, hbH));
                    nextCloudIndex = (nextCloudIndex + 1) % 5;
                    spawnTimer = 0;
                }

                for (int i = obstacles.size() - 1; i >= 0; i--) {
                    Obstacle obs = obstacles.get(i);
                    obs.move();

                    if (obs.getY() + CLOUD_HEIGHT < player.getY() && !scoredObstacles.contains(obs)) {
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
                        homeButton.setVisible(true);
                        gameTimer.stop();
                    }
                }

                tickCount++;
                if (tickCount % DIFFICULTY_INTERVAL == 0) {
                    if (spawnInterval > 10) {
                        spawnInterval -= 10;
                    }
                    if (obstacleSpeed < 8) {
                        obstacleSpeed++;
                    }
                }

                bgY -= (obstacleSpeed / 2 + 1);
                if (bgY <= -BG_DRAW_HEIGHT) {
                    bgY = 0;
                }

                repaint();
            }
        });

        replayButton = new JButton("");
        replayButton.setFont(starCrushFont.deriveFont(Font.PLAIN, 20f));
        replayButton.setIcon(replayIcon);
        replayButton.setHorizontalTextPosition(JButton.RIGHT);
        replayButton.setIconTextGap(10);
        replayButton.setContentAreaFilled(false);
        replayButton.setBorderPainted(false);
        replayButton.setFocusPainted(false);
        replayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                score = 0;
                gameOver = false;
                player = new Player(180, 400, 45, 360, parachuteColor, selectedCatIndex);
                obstacles.clear();
                scoredObstacles.clear();
                nextCloudIndex = 0;
                int hbW = 40, hbH = 40;
                if (cloudImages[nextCloudIndex] != null) {
                    hbH = CLOUD_HEIGHT;
                    hbW = cloudImages[nextCloudIndex].getWidth() * CLOUD_HEIGHT / cloudImages[nextCloudIndex].getHeight();
                }
                obstacles.add(new Obstacle(400, obstacleSpeed, nextCloudIndex, hbW, hbH));
                nextCloudIndex = (nextCloudIndex + 1) % 5;
                spawnTimer = 0;
                obstacleSpeed = 5;
                spawnInterval = 60;
                tickCount = 0;
                bgY = 0;
                replayButton.setVisible(false);
                homeButton.setVisible(false);
                gameTimer.start();
                requestFocusInWindow();
            }
        });
        replayButton.setVisible(false);
        replayButton.setBounds(100, 480, 80, 80);
        this.add(replayButton);

        playButton = new JButton("");
        playButton.setFont(starCrushFont.deriveFont(Font.PLAIN, 20f));
        playButton.setIcon(playIcon);
        playButton.setHorizontalTextPosition(JButton.RIGHT);
        playButton.setIconTextGap(10);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        playButton.setForeground(Color.WHITE);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onHomeScreen = false;
                playButton.setVisible(false);
                customizeButton.setVisible(false);
                score = 0;
                gameOver = false;
                player = new Player(180, 400, 45, 360, parachuteColor, selectedCatIndex);
                obstacles.clear();
                scoredObstacles.clear();
                nextCloudIndex = 0;
                int hbW = 40, hbH = 40;
                if (cloudImages[nextCloudIndex] != null) {
                    hbH = CLOUD_HEIGHT;
                    hbW = cloudImages[nextCloudIndex].getWidth() * CLOUD_HEIGHT / cloudImages[nextCloudIndex].getHeight();
                }
                obstacles.add(new Obstacle(400, obstacleSpeed, nextCloudIndex, hbW, hbH));
                nextCloudIndex = (nextCloudIndex + 1) % 5;
                spawnTimer = 0;
                obstacleSpeed = 5;
                spawnInterval = 60;
                tickCount = 0;
                gameTimer.start();
                requestFocusInWindow();
            }
        });
        playButton.setVisible(true);
        playButton.setBounds(120, 420, 80, 80);
        this.add(playButton);

        homeButton = new JButton("");
        homeButton.setFont(starCrushFont.deriveFont(Font.PLAIN, 20f));
        homeButton.setIcon(homeIcon);
        homeButton.setHorizontalTextPosition(JButton.RIGHT);
        homeButton.setIconTextGap(10);
        homeButton.setContentAreaFilled(false);
        homeButton.setBorderPainted(false);
        homeButton.setFocusPainted(false);
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameOver = false;
                onHomeScreen = true;
                homeButton.setVisible(false);
                replayButton.setVisible(false);
                score = 0;
                player = new Player(180, 400, 45, 360, parachuteColor, selectedCatIndex);
                obstacles.clear();
                scoredObstacles.clear();
                nextCloudIndex = 0;
                int hbW = 40, hbH = 40;
                if (cloudImages[nextCloudIndex] != null) {
                    hbH = CLOUD_HEIGHT;
                    hbW = cloudImages[nextCloudIndex].getWidth() * CLOUD_HEIGHT / cloudImages[nextCloudIndex].getHeight();
                }
                obstacles.add(new Obstacle(400, obstacleSpeed, nextCloudIndex, hbW, hbH));
                nextCloudIndex = (nextCloudIndex + 1) % 5;
                spawnTimer = 0;
                obstacleSpeed = 5;
                spawnInterval = 60;
                tickCount = 0;
                bgY = 0;
                playButton.setVisible(true);
                customizeButton.setVisible(true);
                repaint();
            }
        });
        homeButton.setVisible(false);
        homeButton.setBounds(220, 480, 80, 80);
        this.add(homeButton);

        customizeButton = new JButton("");
        customizeButton.setIcon(customizeIcon);
        customizeButton.setContentAreaFilled(false);
        customizeButton.setBorderPainted(false);
        customizeButton.setFocusPainted(false);
        customizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onHomeScreen = false;
                onCustomizeScreen = true;
                playButton.setVisible(false);
                customizeButton.setVisible(false);
                for (int i = 0; i < swatchButtons.length; i++) {
                    swatchButtons[i].setVisible(true);
                }
                for (int i = 0; i < catButtons.length; i++) {
                    catButtons[i].setVisible(true);
                }
                backFromCustomizeButton.setVisible(true);
                repaint();
            }
        });
        customizeButton.setVisible(true);
        customizeButton.setBounds(220, 420, 80, 80);
        this.add(customizeButton);

        for (int i = 0; i < swatchButtons.length; i++) {
            final int index = i;
            swatchButtons[i] = new JButton("");
            swatchButtons[i].setBackground(SWATCH_COLORS[i]);
            swatchButtons[i].setOpaque(true);
            swatchButtons[i].setContentAreaFilled(true);
            swatchButtons[i].setFocusPainted(false);
            if (i == selectedSwatchIndex) {
                swatchButtons[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            } else {
                swatchButtons[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            }
            swatchButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parachuteColor = SWATCH_COLORS[index];
                    selectedSwatchIndex = index;
                    for (int j = 0; j < swatchButtons.length; j++) {
                        if (j == selectedSwatchIndex) {
                            swatchButtons[j].setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                        } else {
                            swatchButtons[j].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                        }
                    }
                    repaint();
                }
            });
            swatchButtons[i].setVisible(false);
            swatchButtons[i].setBounds(20 + i * 45, 430, 40, 40);
            this.add(swatchButtons[i]);
        }

        for (int i = 0; i < catButtons.length; i++) {
            final int index = i;
            catButtons[i] = new JButton("");
            catButtons[i].setContentAreaFilled(false);
            catButtons[i].setFocusPainted(false);
            if (catImages[i] != null) {
                catButtons[i].setIcon(new ImageIcon(catImages[i].getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            }
            if (i == selectedCatIndex) {
                catButtons[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            } else {
                catButtons[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            }
            catButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedCatIndex = index;
                    for (int j = 0; j < catButtons.length; j++) {
                        if (j == selectedCatIndex) {
                            catButtons[j].setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                        } else {
                            catButtons[j].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                        }
                    }
                    repaint();
                }
            });
            catButtons[i].setVisible(false);
            catButtons[i].setBounds(35 + i * 55, 490, 40, 40);
            this.add(catButtons[i]);
        }

        backFromCustomizeButton = new JButton("");
        backFromCustomizeButton.setIcon(homeIcon);
        backFromCustomizeButton.setContentAreaFilled(false);
        backFromCustomizeButton.setBorderPainted(false);
        backFromCustomizeButton.setFocusPainted(false);
        backFromCustomizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCustomizeScreen = false;
                onHomeScreen = true;
                for (int i = 0; i < swatchButtons.length; i++) {
                    swatchButtons[i].setVisible(false);
                }
                for (int i = 0; i < catButtons.length; i++) {
                    catButtons[i].setVisible(false);
                }
                backFromCustomizeButton.setVisible(false);
                playButton.setVisible(true);
                customizeButton.setVisible(true);
                repaint();
            }
        });
        backFromCustomizeButton.setVisible(false);
        backFromCustomizeButton.setBounds(160, 650, 80, 80);
        this.add(backFromCustomizeButton);
    }

    private boolean collides(Obstacle obs) {
        int playerX = player.getX();
        int playerY = player.getY();
        int playerSize = player.getDiameter();

        int obstacleX = obs.getX();
        int obstacleY = obs.getY();
        int hitboxWidth = obs.getHitboxWidth();
        int hitboxHeight = obs.getHitboxHeight();

        return playerX < obstacleX + hitboxWidth &&
                playerX + playerSize > obstacleX &&
                playerY < obstacleY + hitboxHeight &&
                playerY + playerSize > obstacleY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (onCustomizeScreen) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            Font titleFont = starCrushFont.deriveFont(Font.PLAIN, 48f);
            g.setFont(titleFont);
            String title = "CUSTOMIZE";
            FontMetrics fm = g.getFontMetrics();
            int titleX = (getWidth() - fm.stringWidth(title)) / 2;
            g.drawString(title, titleX, 150);

            Font labelFont = starCrushFont.deriveFont(Font.PLAIN, 20f);
            g.setFont(labelFont);
            String label = "PREVIEW";
            fm = g.getFontMetrics();
            int labelX = (getWidth() - fm.stringWidth(label)) / 2;
            g.drawString(label, labelX, 230);

            int previewX = (getWidth() - 120) / 2;
            int previewY = 260;
            int previewSize = 120;
            int previewBoxHeight = 150;

            g.setColor(Color.WHITE);
            g.fillRect(previewX, previewY, previewSize, previewBoxHeight);

            if (parachuteFilling != null) {
                BufferedImage tinted = new BufferedImage(previewSize, previewSize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D tg = tinted.createGraphics();
                tg.drawImage(parachuteFilling, 0, 0, previewSize, previewSize, null);
                tg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
                tg.setColor(parachuteColor);
                tg.fillRect(0, 0, previewSize, previewSize);
                tg.dispose();
                g.drawImage(tinted, previewX, previewY, null);
            }
            if (parachuteOutline != null) {
                g.drawImage(parachuteOutline, previewX, previewY, previewSize, previewSize, this);
            }
            // Draw cat preview centered at bottom of parachute
            if (catImages[selectedCatIndex] != null) {
                int previewCatSize = 55;
                int catPreviewX = previewX + (previewSize - previewCatSize) / 2 + 5;
                int catPreviewY = previewY + previewSize - previewCatSize / 2 - 8;
                g.drawImage(catImages[selectedCatIndex], catPreviewX, catPreviewY, previewCatSize, previewCatSize, this);
            }

            return;
        }

        if (onHomeScreen) {
            if (startBgImage != null) {
                g.drawImage(startBgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            g.setColor(Color.WHITE);
            Font titleFont = starCrushFont.deriveFont(Font.PLAIN, 48f);
            g.setFont(titleFont);
            String title = "Skydiver";
            FontMetrics fm = g.getFontMetrics();
            int titleX = (getWidth() - fm.stringWidth(title)) / 2;
            g.drawString(title, titleX, 300);

            Font hsFont = starCrushFont.deriveFont(Font.PLAIN, 28f);
            g.setFont(hsFont);
            String hsText = "HIGH SCORE: " + highScore;
            fm = g.getFontMetrics();
            int hsX = (getWidth() - fm.stringWidth(hsText)) / 2;
            g.drawString(hsText, hsX, 370);

            return;
        }

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            Font gameOverFont = starCrushFont.deriveFont(Font.PLAIN, 48f);
            g.setFont(gameOverFont);
            String gameOverText = "GAME OVER";
            FontMetrics fm = g.getFontMetrics();
            int gameOverX = (getWidth() - fm.stringWidth(gameOverText)) / 2;
            g.drawString(gameOverText, gameOverX, 300);

            Font scoreFont = starCrushFont.deriveFont(Font.PLAIN, 28f);
            g.setFont(scoreFont);
            String scoreText = "SCORE: " + score;
            fm = g.getFontMetrics();
            int scoreX = (getWidth() - fm.stringWidth(scoreText)) / 2;
            g.drawString(scoreText, scoreX, 370);

            String highScoreText = "HIGH SCORE: " + highScore;
            fm = g.getFontMetrics();
            int highScoreX = (getWidth() - fm.stringWidth(highScoreText)) / 2;
            g.drawString(highScoreText, highScoreX, 420);
        } else {
            if (bgImage != null) {
                g.drawImage(bgImage, 0, bgY, BG_DRAW_WIDTH, BG_DRAW_HEIGHT, this);
                g.drawImage(bgImage, 0, bgY + BG_DRAW_HEIGHT, BG_DRAW_WIDTH, BG_DRAW_HEIGHT, this);
            }

            int px = player.getX();
            int py = player.getY();
            int pd = PLAYER_DRAW_SIZE;
            if (parachuteFilling != null) {
                BufferedImage tinted = new BufferedImage(pd, pd, BufferedImage.TYPE_INT_ARGB);
                Graphics2D tg = tinted.createGraphics();
                tg.drawImage(parachuteFilling, 0, 0, pd, pd, null);
                tg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
                tg.setColor(player.getParachuteColor());
                tg.fillRect(0, 0, pd, pd);
                tg.dispose();
                g.drawImage(tinted, px, py, null);
            } else {
                g.setColor(Color.RED);
                g.fillOval(px, py, pd, pd);
            }
            if (parachuteOutline != null) {
                g.drawImage(parachuteOutline, px, py, pd, pd, this);
            }
            // Draw cat centered at bottom of parachute
            BufferedImage catImg = catImages[player.getCatIndex()];
            if (catImg != null) {
                int catX = px + (pd - CAT_DRAW_SIZE) / 2 + 3;
                int catY = py + pd - CAT_DRAW_SIZE / 2 - 8;
                g.drawImage(catImg, catX, catY, CAT_DRAW_SIZE, CAT_DRAW_SIZE, this);
            }

            for (int i = 0; i < obstacles.size(); i++) {
                Obstacle obs = obstacles.get(i);
                BufferedImage cloudImg = cloudImages[obs.getImageIndex()];
                if (cloudImg != null) {
                    int drawWidth = cloudImg.getWidth() * CLOUD_HEIGHT / cloudImg.getHeight();
                    g.drawImage(cloudImg, obs.getX(), obs.getY(), drawWidth, CLOUD_HEIGHT, this);
                } else {
                    g.setColor(Color.GRAY);
                    g.fillRect(obs.getX(), obs.getY(), obs.getSize(), obs.getSize());
                }
            }

            g.setColor(Color.WHITE);
            Font scoreFont = starCrushFont.deriveFont(Font.PLAIN, 20f);
            g.setFont(scoreFont);
            g.drawString("SCORE: " + score, 10, 25);
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