import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
    public static final String USERNAME = System.getProperty("user.name");
    public static final String SYSTEMNAME = "System";
    public static final String PAUSE = "■";
    public static final String CONTINUE = "▶";
    public static final String SAVED = "Saved";

    public static Map<String, String> DEF_SETTING = new HashMap<>();
    public static Stage mainStage;

    public static void main(String[] args) {
        initalizeDefault();

        if (args.length > 0) {
            File archive = new File(args[0]);
            if (archive.exists()) {
                mainStage = new Stage("stage", (Map) Register.readObject(archive));
                return;
            }
        }
        mainStage = new Stage("stage");
    }

    public static void initalizeDefault() {
        //property
        Brick.BRICK_SIZE = new Dimension(SCREEN.width / 20, SCREEN.width / 20);
    }

    public static void saveGameData() {
        StatusBar status = mainStage.getStatus();
        if (!status.gameOver) {
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");
            String time = dateFormat.format(now);

            System.out.println(time);
            Register.writeObject(mainStage.dataMap(), new File(status.getPlayer() + time + ".trec"));
        }
    }

    public static void saveBestRecord() {
        StatusBar status = mainStage.getStatus();
        Map data = mainStage.dataMap();
        if (Integer.valueOf((String) status.getBestRecord().get(Register.KEY_SCORE)) <= status.getScore()) {
            Register.setBestScore(data, status.getGenatorIndex());
        }
    }


}