public class Player {
    private int x;
    private int y;
    private int diameter;

    // Window width for bounds checking
    private int windowWidth;

    // Constructor: initialize player position and size
    public Player(int startX, int startY, int diameter, int windowWidth) {
        this.x = startX;
        this.y = startY;
        this.diameter = diameter;
        this.windowWidth = windowWidth;
    }

    // Move the player left or right by dx pixels
    // Keeps the player within bounds (x cannot go below 0 or above windowWidth - diameter)
    public void move(int dx) {
        x += dx;

        // Bounds checking
        if (x < 0) {
            x = 0;
        } else if (x > windowWidth - diameter) {
            x = windowWidth - diameter;
        }
    }

    // Get the x-coordinate of the player
    public int getX() {
        return x;
    }

    // Get the y-coordinate of the player
    public int getY() {
        return y;
    }

    // Get the diameter of the player circle
    public int getDiameter() {
        return diameter;
    }
}
