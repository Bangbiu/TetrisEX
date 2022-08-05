import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import java.util.function.Function;

public class Stage extends JFrame {
    private Timer ticker = new Timer();

    private boolean tickerOn;

    private Tetromino falling;
    private Iterator<String> fGenator;
    private Brick[][] brickSet;
    private boolean[][] visSet;
    private StatusBar status;
    private ControlPanel ctrlPane;
    private Messager msgr;

    public Stage(String stageName, Map data) {
        super(stageName);

        status = new StatusBar();
        status.setPlayer((String) data.get(Register.KEY_PLAYER));
        status.setPlaced((Integer) data.get(Register.KEY_PLACED));
        status.setGentorIndex((Integer) data.get(Register.KEY_MODE));
        status.addPoint((Integer) data.get(Register.KEY_SCORE));
        status.setSpeedMulti((float) data.get(Register.KEY_SPEED));

        visSet = (boolean[][]) data.get(Register.KEY_SCENE);
        brickSet = (Brick[][]) data.get(Register.KEY_BLOCKS);
        falling = (Tetromino) data.get(Register.KEY_FALLING);
        status.setPosition(Main.SCREEN.getHeight() - brickSet[0].length * Brick.BRICK_SIZE.getHeight());

        basicSetup();

        excuteAllBricks((brick) -> (addToStage(brick)));

        refresh();
    }

    public Stage(String stageName) {
        super(stageName);

        status = new StatusBar();
        status.setPlayer(Main.USERNAME);
        status.setGentorIndex(4);

        basicSetup();
        buildUpBrickScene();
        status.setPosition(Main.SCREEN.getHeight() - brickSet[0].length * Brick.BRICK_SIZE.getHeight());

        excuteAllBricks((brick) -> (addToStage(brick)));

    }

    private void basicSetup() {
        setLayout(null);

        setSize(Main.SCREEN);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        setVisible(true);

        syncMode();
        ctrlPane = new ControlPanel();
        ctrlPane.setSysControl(true);
        msgr = new Messager();
        addToStage(ctrlPane);
        addToStage(status);
        addToStage(msgr);
        msgr.showMsg(Main.PAUSE);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                operate(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    while (operate(KeyEvent.VK_DOWN));
                }
            }
        });
    }

    public void buildUpBrickScene() {
        int row = (int) (Main.SCREEN.getWidth() / Brick.BRICK_SIZE.getWidth());
        int col = (int) (Main.SCREEN.getHeight() / Brick.BRICK_SIZE.getHeight());

        brickSet = new Brick[row][col];
        visSet = new boolean[row][col];

        for (int x = 0; x < row; x++) {
            for (int y = 0; y < col; y++) {
                brickSet[x][y] = new Brick(x, y);
                brickSet[x][y].setBackground(randomColor());
                //if (Math.random() * 10 > 5) brickSet[x][y].setVisible(true);
            }
        }
    }

    public boolean operate(int key) {
        switch (key) {
            case 0: return false;
            case KeyEvent.VK_SLASH:
                exitGame();
            case KeyEvent.VK_COMMA:
                status.speedDown();
                syncDelta();
                return true;
            case KeyEvent.VK_PERIOD:
                status.speedUp();
                syncDelta();
                return true;
            case KeyEvent.VK_TAB: {
                Main.saveGameData();
                msgr.showMsg(Main.SAVED);
                return true;
            }
            case KeyEvent.VK_ENTER: {
                status.iterateGenratorIndex();
                syncMode();
                msgr.showMsg(GameModeFactory.GenatorMechanism[status.getGenatorIndex()]);
                return true;
            }
            case KeyEvent.VK_SPACE: {
                if (!ctrlPane.pinned) ctrlPane.setVisible(tickerOn);
                if (tickerOn) {
                    msgr.showMsg(Main.PAUSE);
                    ctrlPane.setSysControl(true);
                    stopTick();
                } else {
                    msgr.showMsg(Main.CONTINUE);
                    ctrlPane.setSysControl(false);
                    startTick();
                }
                return true;
            }
        }

        if (falling == null) recreate();
        Tetromino nextState = falling.duplicate();
        switch (key) {
            case KeyEvent.VK_UP : nextState.rotate();resetTimer();break;
            case KeyEvent.VK_LEFT : nextState.moveLeft();resetTimer();break;
            case KeyEvent.VK_RIGHT : nextState.moveRight();resetTimer();break;
            case KeyEvent.VK_DOWN : nextState.moveDown();resetTimer();break;
        }
        boolean toRet = !colided(nextState);
        if (toRet) falling = nextState;
        refresh();
        return toRet;
    }

    public void refresh() {
        for (int x = 0; x < visSet.length; x++) for (int y = 0; y < visSet[x].length; y++) brickSet[x][y].setVisible(visSet[x][y]);
        for (Point pt : falling.getIndexSet()) {
            if (pt.x >=0 && pt.x < visSet.length && pt.y >=0 && pt.y < visSet[0].length) {
                brickSet[pt.x][pt.y].setVisible(true);
                brickSet[pt.x][pt.y].setBackground(falling.backColor());
            }
        }

    }

    private boolean colided(Tetromino tet) {
        for (Point cell : tet.getIndexSet()) {
            if (cell.x >= brickSet.length || cell.x < 0) return true;
            if (cell.y >= brickSet[0].length) return true;
            if (cell.y < 0) continue;

            if (visSet[cell.x][cell.y]) return true;
        }
        return false;
    }

    public void clear() {
        int clearLayer = 0;
        int rightmost = visSet.length;
        int bottom = visSet[0].length;
        for (int y = bottom - 1 ; y >= 0; y--){
            int visCount = 0;
            for (int x = 0; x < rightmost; x++) if (visSet[x][y]) visCount++;
            if (visCount == rightmost) {
                for (int cy = y; cy > 0; cy--) {
                    for (int x = 0; x < rightmost; x++) {
                        visSet[x][cy] = visSet[x][cy - 1];
                    }
                }
                for (int x = 0; x < rightmost; x++) visSet[x][0] = false;
                clearLayer++;
                y++;
            }
        }
        status.addPoint(clearLayer * clearLayer);
    }

    public void release() {
        for (Point pt : falling.getIndexSet()) {
            if (pt.y < 0) {
                status.gameOver = true;
                exitGame();
            }
            visSet[pt.x][pt.y] = true;
        }
        status.gainPlaced();
    }

    public void recreate() {
        falling = new Tetromino(brickSet.length / 2, 0, randomColor(),fGenator.next());
    }

    public void excuteAllBricks(Function<Brick, Boolean> func) {
        for (Brick[] rows : brickSet) {
            for (Brick brick : rows) {
                func.apply(brick);
            }
        }
    }

    public void paint(Graphics g) {
        //g.clearRect(0, 0, Main.SCREEN.width, Main.SCREEN.height);
        super.paint(g);
        Graphics2D canv = (Graphics2D)g;

        repaint();
    }

    public Map dataMap() {
        return Register.mapOf(
                Register.KEY_SCORE, status.getScore(),
                Register.KEY_SPEED, status.getSpdMulti(),
                Register.KEY_MODE, status.getGenatorIndex(),
                Register.KEY_PLAYER, status.getPlayer(),
                Register.KEY_PLACED, status.getPlacedCount(),
                Register.KEY_SCENE, visSet,
                Register.KEY_BLOCKS, brickSet,
                Register.KEY_FALLING, falling
        );
    }

    public StatusBar getStatus() {
        return status;
    }

    public boolean addToStage(JComponent comp) {
        getContentPane().add(comp);
        //repaint();
        return true;
    }

    public void syncMode() {
        fGenator = GameModeFactory.tetroGenerator(status.getGenatorIndex());
    }

    public void syncDelta() {
        if (tickerOn) {
            stopTick();
            startTick();
        }
        msgr.showMsg("X " + status.getSpdMulti());
    }

    public void resetTimer() {
        if (tickerOn) {
            stopTick();
            startTick();
        }
    }

    public void startTick() {
        this.ticker = new Timer();
        this.ticker.schedule(new TickTask(), status.getDelta(), status.getDelta());
        tickerOn = true;
    }

    public void stopTick() {
        this.ticker.cancel();
        tickerOn = false;
    }

    public void exitGame() {
        Main.saveBestRecord();
        System.exit(1);
    }

    public static Color randomColor() {
        return  new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }


    private class  TickTask extends TimerTask {
        public void run() {
            if (!isFocusOwner()) {
                if (tickerOn) operate(KeyEvent.VK_SPACE);
                return;
            }
            if (!operate(KeyEvent.VK_DOWN)) {
                release();
                recreate();
                clear();
            }
        }
    }

    private class ControlPanel extends JPanel {
        public boolean pinned;

        private int size;
        /*
            SAVE, CONTROL ,EXIST
            SLOWER, UP ,FASTER
            LEFT, DOWN ,RIGHT
         */
        public ControlPanel() {
            setBackground(new Color(0, 0, 0, 150));
            size = Math.min(Main.SCREEN.width, Main.SCREEN.height) / 3;
            pinned = false;
            setSize(size, size);
            setLocation(0, 0);
            setLayout(new GridLayout(3, 3, 5, 5));

            //Buttons
            add(new ControlButton('⇲', KeyEvent.VK_TAB, KeyEvent.VK_TAB, false));
            add(new ControlButton('◉', KeyEvent.VK_SPACE, KeyEvent.VK_SPACE, false));
            add(new ControlButton('╳', KeyEvent.VK_SLASH, KeyEvent.VK_SLASH, false));
            add(new ControlButton('⇤', KeyEvent.VK_COMMA, KeyEvent.VK_COMMA, false));
            add(new ControlButton('▲', KeyEvent.VK_UP, KeyEvent.VK_ENTER, true));
            add(new ControlButton('⇥', KeyEvent.VK_PERIOD, KeyEvent.VK_PERIOD, false));
            add(new ControlButton('◀', KeyEvent.VK_LEFT, 0, true)) ;
            add(new ControlButton('▼', KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, true));
            add(new ControlButton('▶', KeyEvent.VK_RIGHT, 0, true));

            setVisible(true);
        }

        public void setSysControl(boolean visibility) {
            getComponent(0).setVisible(visibility);
            //getComponent(1).setVisible(visibility);
            getComponent(2).setVisible(visibility);
            getComponent(3).setVisible(visibility);
            getComponent(5).setVisible(visibility);
            ((JLabel) getComponent(4)).setText(visibility ? "⊕" : "▲");
            ((JLabel) getComponent(7)).setText(visibility ? "※" : "▼");
        }

        public void shift(int x, int y) {
            this.setLocation(getX() + x, getY() + y);
        }

        private class ControlButton extends JLabel {

            private int draggedAtX, draggedAtY;

            public ControlButton(char sign, int perfKey, int pauseKey, boolean isController) {
                super(String.valueOf(sign));

                setOpaque(isController);
                setText(String.valueOf(sign));
                setFont(new Font("", Font.BOLD, 20));
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(Color.white);
                setBackground(Color.black);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (tickerOn) {
                            operate(perfKey);
                            if (perfKey == KeyEvent.VK_DOWN) while (operate(perfKey));
                        } else {
                            if (pauseKey == KeyEvent.VK_DOWN) {
                                pinned = !pinned;
                                msgr.showMsg((pinned ? "" :"Un") + "Pin");
                            } else {
                                operate(pauseKey);
                            }
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        if (isController) {
                            setForeground(Color.white);
                            setBackground(Color.black);
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        draggedAtX = e.getX();
                        draggedAtY = e.getY();
                        if (isController) {
                            setForeground(Color.black);
                            setBackground(Color.white);
                        }
                    }

                });

                addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseDragged(MouseEvent e) {
                        if (!isController) shift(e.getX() - draggedAtX, e.getY() - draggedAtY);
                    }
                });

            }
        }
    }
}