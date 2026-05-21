public class Obstacle {
    private int x;
    private int y;
    private int size;
    private int windowWidth;

    // Constructor: initializes obstacle with given window width and sets initial position
    public Obstacle(int windowWidth) {
        this.windowWidth = windowWidth;
        this.size = 40;
        respawn();
    }

    // respawn() method: positions obstacle just below screen at random horizontal position
    public void respawn() {
        y = 800;
        x = (int) (Math.random() * (windowWidth - size));
    }

    // move() method: moves obstacle upward (decrement y)
    public void move() {
        y -= 3;
    }

    // isOffScreen() method: returns true when obstacle has moved past top of screen
    public boolean isOffScreen() {
        return y + size < 0;
    }

    // Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }
}
