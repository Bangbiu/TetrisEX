import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class Tetromino implements Serializable {

    public List<Point> cells;

    private Point center;
    private Color back;

    public Tetromino(int centerX, int centerY, Color backcolor,String code) {
        center = new Point(centerX, centerY);
        cells = new LinkedList<>();
        back = backcolor;
        for (Point pt : Tetromino.interpret(code)) cells.add(pt);
    }

    public Tetromino(Tetromino other) {
        this.cells = new LinkedList<>();
        for (Point cell : other.cells) {
            this.cells.add(new Point(cell));
        }
        this.back = other.backColor();
        this.center = new Point(other.center);
    }

    public Color backColor() {
        return back;
    }

    public Point getCenter() {
        return center;
    }

    public List<Point> getIndexSet() {
        List<Point> toRet = new LinkedList<>();
        for (Point cell : cells) {
            toRet.add(new Point(center.x + cell.x, center.y + cell.y));
        }
        return toRet;
    }

    public void rotate() {
        for (Point cell : cells) {
            int temp = cell.x;
            cell.x = cell.y;
            cell.y = -temp;
        }
    }

    public void moveLeft() { center.x--;}

    public void moveRight() { center.x++;}

    public void moveDown() { center.y++;}

    public Tetromino duplicate() {
        return new Tetromino(this);
    }

    public static List<Point> interpret(String code) {
        List<Point> toRet = new LinkedList<>();
        int count = 0, layer = 0;
        Point cur = new Point(0, 0);
        for (char bin : code.toCharArray()) {
            if (bin == '1') {
                toRet.add(new Point(cur));
            }
            count++;
            if (count >= Math.pow(layer * 2 + 1, 2)) {
                cur.x--;
                layer++;
            } else {
                if (cur.x == -layer) {
                    if (cur.y > -layer)
                        cur.y--;
                    else
                        cur.x++;
                } else if (cur.y == -layer) {
                    if (cur.x < layer)
                        cur.x++;
                    else
                        cur.y++;
                } else if (cur.x == layer) {
                    if (cur.y < layer)
                        cur.y++;
                    else
                        cur.x--;
                } else if (cur.y == layer) {
                        cur.x--;
                }
            }
        }
        return toRet;
        //1 , 9 , 25
    }
}
