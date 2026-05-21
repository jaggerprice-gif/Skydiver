import java.awt.Color;

public class Player {
    private int x;
    private int y;
    private int diameter;
    private int maxX;
    private Color parachuteColor;

    public Player(int x, int y, int diameter, int maxX, Color parachuteColor) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.maxX = maxX;
        this.parachuteColor = parachuteColor;
    }

    public void move(int dx) {
        x += dx;
        if (x < 0) x = 0;
        if (x > maxX) x = maxX;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getDiameter() { return diameter; }
    public Color getParachuteColor() { return parachuteColor; }
    public void setParachuteColor(Color c) { parachuteColor = c; }
}