import java.util.Random;

public class Obstacle {
    private int x;
    private int y;
    private int size = 40;
    private int speed;
    private int imageIndex;  // which cloud image to use (0–4)
    private int hitboxSize = 40;  // used for collision detection, approximates visible cloud area

    public Obstacle(int windowWidth, int speed, int imageIndex) {
        Random rand = new Random();
        this.x = rand.nextInt(windowWidth - size);
        this.y = 800;
        this.speed = speed;
        this.imageIndex = imageIndex;
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
    public int getHitboxSize() { return hitboxSize; }
}
