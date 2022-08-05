import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.*;
public class Register {
    // HKEY_LOCAL_MACHINE\Software\JavaSoft\prefs

    public static final String KEY_SCORE = "score";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_MODE = "gamemode";
    public static final String KEY_PLACED = "placed";
    public static final String KEY_PLAYER = "player";

    public static final String KEY_SCENE = "scene";
    public static final String KEY_BLOCKS = "blocks";
    public static final String KEY_FALLING = "falling";

    public static void setBestScore(Map record, int mode) {
        Preferences pref = Preferences.systemRoot().node("/tetris/" + mode);
        for (Object key : record.keySet()) {
            pref.put((String) key, String.valueOf(record.get(key)));
        }
    }
    public static Map getBestScore(int mode) {
        Preferences pref = Preferences.systemRoot().node("/tetris/" + mode);
        return mapOf(
                KEY_SCORE, pref.get(KEY_SCORE, "0"),
                KEY_SPEED, pref.get(KEY_SPEED, "1"),
                KEY_PLACED, pref.get(KEY_PLACED, "0"),
                KEY_MODE, pref.get(KEY_MODE, String.valueOf(mode)),
                KEY_PLAYER, pref.get(KEY_PLAYER, "Empty")
        );
    }

    public static Map<Object, Object> mapOf(Object... kVs) {
        Map<Object, Object> toRet = new HashMap<>();
        for (int i = 0; i < kVs.length; i+=2) {
            toRet.put(kVs[i], kVs[i + 1]);
        }
        return toRet;
    }

    public static void writeObject(Object src, File path) {
        byte[] toWrite = serialize(src);
        if (toWrite != null) writeContents(path, toWrite);
    }

    public static Object readObject(File path) {
        if (!(path.exists() && path.isFile())) {
            return null;
        }

        Object output;
        File inFile = path;
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(inFile));
            output = inp.readObject();
            inp.close();
            return output;
        } catch (IOException | ClassNotFoundException excp) {
            return null;
        }
    }

    private static byte[] serialize(Object src) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(src);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            System.out.println("Unable to Serialize : " + excp.getMessage());
            return null;
        }
    }
    /* Write the entire contents of BYTES to FILE, creating or overwriting it as
       needed. Throws IllegalArgumentException in case of problems. */
    private static void writeContents(File file, byte[] bytes) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            Files.write(file.toPath(), bytes);
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

}
