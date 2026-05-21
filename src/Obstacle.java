public class Obstacle {
    private int x;
    private int y;
    private int size;
    private int windowWidth;
    // Speed of the obstacle — increases with difficulty to make the game harder
    private int speed;

    public Obstacle(int windowWidth, int speed) {
        this.windowWidth = windowWidth;
        this.size = 40;
        this.speed = speed;
        respawn();
    }

    public void respawn() {
        y = 800;
        x = (int) (Math.random() * (windowWidth - size));
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
}
