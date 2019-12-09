import com.google.common.hash.HashCode;

import java.security.SecureRandom;
import java.util.*;

public class RainbowTableCreator extends HashEngine {

    private static DbWorker db = new DbWorker();

    private void createReductionFunctions() {
        final SecureRandom random = new SecureRandom();
        for (int i = 0; i < CHAIN_LENGTH; i++) {
            List<Integer> reduction = new ArrayList<>();
            for (int j = 0; j < HASH_SIZE / 2; j++) {
                reduction.add(random.nextInt(HASH_SIZE));
            }
            reductions.add(reduction);
        }
        db.addToDatabase(reductions);
    }

    private String getRandomPassword() {
        final SecureRandom random = new SecureRandom();
        return getRandomWord(random);
    }

    private void createChain() {
        String password = getRandomPassword();
        HashCode hash = getHash(password);
        for (int i = 0; i < CHAIN_LENGTH; i++) {
            String pass = getReductionPassword(hash.asBytes(), i);
            hash = getHash(pass);
        }
        db.addToDatabase(password, hash.toString());
    }

    public void createTable(){
        createReductionFunctions();
        for (int i = 0; i < CHAIN_NUMBER; i++) {
            if(i % 10000000 == 0){
                System.out.println(i);
            }
            createChain();
        }
        finish();
    }

    private void finish() {
        db.finishWorkWithDb();
    }

    public static void main(String[] args) {
        RainbowTableCreator creator = new RainbowTableCreator();
        creator.createTable();
    }

}
