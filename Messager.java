import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Messager extends JPanel {
    private JLabel display = new JLabel();
    private Timer ticker = new Timer();
    private int prg = 0;
    private final static int SIZE = Main.SCREEN.height / 5;
    private final static int LEFT = (Main.SCREEN.width - SIZE) / 2;
    private final static int TOP = (Main.SCREEN.height - SIZE) / 2;

    private boolean tickerOn = false;

    public Messager() {
        setBackground(new Color(0, 0, 0, 0));
        setSize(SIZE, SIZE);
        setLocation(LEFT, TOP);
        setLayout(new BorderLayout());

        display.setFont(new Font("", Font.BOLD, 50));
        display.setForeground(Color.white);
        display.setHorizontalAlignment(SwingConstants.CENTER);
        add(display, BorderLayout.CENTER);
        setVisible(false);

    }

    public void showMsg(String msgText) {
        display.setText(msgText);
        setVisible(true);
        if (tickerOn) reset();
        this.ticker.schedule(new fadeOut(), 1, 1);
        tickerOn = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (prg > 255) return;
        g.setColor(new Color(0, 0, 0, 255 - prg));
        g.fillOval(0, 0, getWidth(), getHeight());
    }

    private class fadeOut extends TimerTask {
        public void run() {
            display.setForeground(new Color(255, 255, 255, 255 - prg));
            setSize(SIZE + prg / 10, SIZE + prg / 10);
            setLocation(LEFT - prg / 20, TOP - prg / 20);
            display.setSize(getWidth(), getHeight());
            prg += 5;
            if (prg > 255) {
                reset();
                setVisible(false);
            }
        }
    }

    private void reset() {
        ticker.cancel();
        ticker = new Timer();
        tickerOn = false;
        prg = 0;
    }

}
