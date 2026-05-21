import java.util.Random;

public class Obstacle {
    private int x;
    private int y;
    private int size = 40;
    private int speed;
    private int imageIndex;  // which cloud image to use (0–4)
    private int hitboxWidth;   // collision width — matches drawn image width
    private int hitboxHeight;  // collision height — matches drawn image height

    public Obstacle(int windowWidth, int speed, int imageIndex, int hitboxWidth, int hitboxHeight) {
        Random rand = new Random();
        this.x = rand.nextInt(windowWidth - size);
        this.y = 800;
        this.speed = speed;
        this.imageIndex = imageIndex;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
    }

    public void move() {
        y -= speed;
    }

    public boolean isOffScreen() {
        return y + size < 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
    public int getImageIndex() { return imageIndex; }
    public int getHitboxWidth() { return hitboxWidth; }
    public int getHitboxHeight() { return hitboxHeight; }
}