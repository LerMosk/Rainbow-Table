import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public abstract class HashEngine {
    protected final static int CHAIN_LENGTH = 250;
    protected final static int CHAIN_NUMBER = 247000000;
    protected final static int HASH_SIZE = 32;
    protected final static int PASSWORD_LENGTH = 10;
    protected final static char[] alphabet = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '{', '}'};

    protected List<List<Integer>> reductions = new ArrayList<>();

    static HashCode getHash(String password) {
        return Hashing.sha256().hashString(password, StandardCharsets.UTF_8);
    }

    protected String getReductionPassword(byte[] hash, int step) {
        List<Integer> reduction = reductions.get(step);
        final SecureRandom random = new SecureRandom(getInitArray(hash, reduction));
        return getRandomWord(random);
    }

    protected String getRandomWord(SecureRandom random) {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(alphabet[random.nextInt(alphabet.length)]);
        }
        return password.toString();
    }

    protected byte[] getInitArray(byte[] hash, List<Integer> reduction) {
        byte[] initArray = new byte[reduction.size()];
        for (int i = 0; i < reduction.size(); i++) {
            initArray[i] = hash[reduction.get(i)];
        }
        return initArray;
    }

}
