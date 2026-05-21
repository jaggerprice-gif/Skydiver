import java.awt.Color;

public class Player {
    private int x;
    private int y;
    private int diameter;
    private int maxX;
    private Color parachuteColor;
    private int catIndex;

    public Player(int x, int y, int diameter, int maxX, Color parachuteColor, int catIndex) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.maxX = maxX;
        this.parachuteColor = parachuteColor;
        this.catIndex = catIndex;
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
    public int getCatIndex() { return catIndex; }
    public void setCatIndex(int i) { catIndex = i; }
}