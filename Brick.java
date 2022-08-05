import javax.swing.*;
import java.awt.*;

public class Brick extends JPanel {

    public static Dimension BRICK_SIZE = new Dimension(50, 50);

    Color inner = Color.WHITE;

    int row,col;

    public Point getPosition() {
        return new Point(row, col);
    }

    public Brick(int row, int col) {
        setSize(BRICK_SIZE);
        this.row = row;this.col = col;
        setLocation(row * (int) BRICK_SIZE.getWidth(), col * (int) BRICK_SIZE.getHeight());
        setBackground(inner);
        //setBorder(BorderFactory.createLineBorder(border, 1));
        setVisible(false);
    }
}
