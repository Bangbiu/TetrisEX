import java.util.*;

public class GameModeFactory {

    private final static String CLASSIC = "Classic";
    private final static String SINGLE = "Single";
    private final static String PAIR = "Pair";
    private final static String TRIPLE = "Triple";
    private final static String QUADRUPLE = "Quad";
    private final static String QUINTUPLE = "Quint";
    private final static String RANDOMIZED = "RND";
    private final static String MINORRANDOMIZED = "mRND";

    public final static String[] GenatorMechanism = new String[] {CLASSIC, SINGLE, PAIR, TRIPLE, QUADRUPLE, QUINTUPLE, MINORRANDOMIZED, RANDOMIZED};

    private final static Map<Object, Object> TetroMaps = Register.mapOf(
            //HIJKL M NO P QRSTUVWXYZ
    SINGLE, Register.mapOf("DOT", "1"),

    PAIR, Register.mapOf(
            "STICK", "11",
            "BIN_INCLINE", "101",
            "SEPERATED", "010001",
            "SEP_INC", "01001"
    ),

    TRIPLE, Register.mapOf(
            "PLANE", "110001",
            "TRI_J", "1101",
            "TRI_L", "111",
            "TRI_INCLINE", "1010001",
            "TRI_V", "10101",
            "SEP_J", "11001",
            "SEP_L", "101001"
    ),

    QUADRUPLE, Register.mapOf(
            "S", "11011",
            "Z", "101101",
            "L", "10010011",
            "J", "100100011",
            "I", "100100010000001",
            "O", "1111",
            "T", "110101"
    ),

    QUINTUPLE, Register.mapOf(
            "C", "110001101",
            "QUI_S", "1110011",
            "QUI_Z", "10110011",
            "QUI_I", "1100010000100000001",
            "QUI_CROSS", "11010101",
            "L_GUN", "11100101",
            "R_GUN", "11001101",
            "R_THUMB", "111101",
            "L_THUMB", "11111",
            "SEP_BLOCK","1111001"
    )

    );

    public static Iterator<String> tetroGenerator(int mode) {
        return new TetrominoGenerator(GenatorMechanism[mode]);
    }

    private static class TetrominoGenerator implements Iterator<String> {
        private List<String> randomSet;
        private String includes;
        public TetrominoGenerator(String including) {
            super();
            includes = including;
            if (including.indexOf(RANDOMIZED) != -1) return;
            if (including.equals(CLASSIC)){randomSet = getClassicSet(); return;}
            randomSet = new LinkedList<>();
            for (String key : GenatorMechanism) {
                if (key.equals(CLASSIC)) continue;
                Map combMap = (Map) TetroMaps.get(key);
                for (Object tetroName : combMap.keySet()) randomSet.add((String) combMap.get(tetroName));
                if (key.equals(including)) break;
            }
        }

        private List<String> getClassicSet() {
            List<String> toRet = new LinkedList<>();
            Map clsMap = (Map) TetroMaps.get(QUADRUPLE);
            for (Object key : clsMap.keySet()) toRet.add((String) clsMap.get(key));
            return toRet;
        }

        public boolean hasNext() {
            return true;
        }

        public String next() {
            Random rand = new Random(System.currentTimeMillis());
            if (includes.indexOf(RANDOMIZED) != -1) {
                String toRet = new String();
                for (int i = 0; i < (includes.equals(RANDOMIZED) ? 24 : 8); i++) {
                    if (rand.nextInt(100) <(includes.equals(RANDOMIZED) ? 84 : 55)) toRet += '0';
                    else toRet += '1';
                }
                if (toRet.indexOf('1') == -1) toRet = "1";
                return toRet;
            } else {
                return randomSet.get(rand.nextInt(randomSet.size()));
            }
        }
    }
}

