import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StatusBar extends JPanel {

    public boolean gameOver;

    private final static String LBL_SCORE = "Score : ";
    private final static String LBL_COUNT = "Placed : ";
    private final static String LBL_MODE = "GameMode : ";
    private final static String LBL_PLAYER = "Player : ";
    private final static String LBL_SPEED = "speed : ";

    private int deltaStd = 300, delta = deltaStd;
    private float deltaDiv = 1;
    private int genIndex;

    private Map bestRec;
    private int score = 0, plcd = 0;
    private String playerName = "";

    private DataBoard current, best;


    public StatusBar() {
        setBackground(new Color(0, 0, 0));

        setVisible(true);

        current = new DataBoard(Color.WHITE);
        best = new DataBoard(Color.YELLOW);

        add(current);
        add(best);

        current.refresh();
        best.refresh(Register.getBestScore(genIndex));
    }

    public void setPosition(double height) {
        setSize(Main.SCREEN.width,(int) height);
        setLocation(0, (int) (Main.SCREEN.getHeight() - height));
    }

    public float getSpdMulti() {
        return deltaDiv;
    }

    public void speedUp() {
        deltaDiv += 0.5;
        delta = (int) (deltaStd / deltaDiv);
        current.refresh();
    }

    public void speedDown() {
        if (deltaDiv > 0.5) deltaDiv -= 0.5;
        delta = (int) (deltaStd / deltaDiv);
        current.refresh();
    }

    public void setSpeedMulti(float spd) {
        deltaDiv = spd;
        delta = (int) (deltaStd / deltaDiv);
        current.refresh();
    }

    public int getDelta() {
        return delta;
    }

    public int getScore() {
        return score;
    }

    public int getPlacedCount() {
        return plcd;
    }

    public void addPoint(int pts) {
        score += pts;
        current.refresh();
    }

    public void gainPlaced() {
        plcd++;
        current.refresh();
    }

    public void setPlaced(int placed) {
        this.plcd = placed;
        current.refresh();
    }

    public int getGenatorIndex() {
        return  genIndex;
    }

    public Map getBestRecord() {
        return bestRec;
    }

    public void setGentorIndex(int index) {
        genIndex = index;
        current.refresh();
        best.refresh(Register.getBestScore(genIndex));
    }

    public String getPlayer() {
        return playerName;
    }

    public void setPlayer(String name) {
        playerName = name;
    }

    public void iterateGenratorIndex() {
        genIndex = (genIndex + 1) % GameModeFactory.GenatorMechanism.length;
        current.refresh();
        best.refresh(Register.getBestScore(genIndex));
    }

    private class DataBoard extends JPanel {
        private JLabel scoreDisplay = new JLabel(LBL_SCORE);
        private JLabel tetroCounter = new JLabel(LBL_COUNT);
        private JLabel playerDisplay = new JLabel(LBL_PLAYER);
        private JLabel modeDisplay = new JLabel(LBL_MODE);
        private JLabel spdDisplay = new JLabel(LBL_MODE);

        public DataBoard(Color txtColor) {

            super();

            setOpaque(false);

            playerDisplay.setForeground(txtColor);
            scoreDisplay.setForeground(txtColor);
            tetroCounter.setForeground(txtColor);
            modeDisplay.setForeground(txtColor);
            spdDisplay.setForeground(txtColor);

            add(playerDisplay);
            add(scoreDisplay);
            add(modeDisplay);
            add(spdDisplay);
            add(tetroCounter);
        }

        public void refresh() {
            scoreDisplay.setText(LBL_SCORE + score);
            tetroCounter.setText(LBL_COUNT + plcd);
            playerDisplay.setText(LBL_PLAYER + playerName);
            modeDisplay.setText(LBL_MODE + GameModeFactory.GenatorMechanism[genIndex]);
            spdDisplay.setText(LBL_SPEED + deltaDiv);
        }

        public void refresh(Map map) {
            bestRec = map;
            scoreDisplay.setText(LBL_SCORE + Integer.valueOf((String) map.get(Register.KEY_SCORE)));
            tetroCounter.setText(LBL_COUNT + Integer.valueOf((String) map.get(Register.KEY_PLACED)));
            playerDisplay.setText(LBL_PLAYER + map.get(Register.KEY_PLAYER));
            modeDisplay.setText(LBL_MODE + GameModeFactory.GenatorMechanism[Integer.valueOf((String) map.get(Register.KEY_MODE))]);
            spdDisplay.setText(LBL_SPEED + Double.valueOf((String) map.get(Register.KEY_SPEED)));
        }
    }

}
