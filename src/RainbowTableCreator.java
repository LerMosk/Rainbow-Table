import com.google.common.hash.HashCode;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class RainbowTableCreator extends HashEngine {

    private static DbWorker db = new DbWorker();

    void createReductionFunctions() {
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

    String getRandomPassword() {
        final SecureRandom random = new SecureRandom();
        return getRandomWord(random);
    }

    void createChain() {
        //List<String> passwords = new LinkedList<>();
        //List<HashCode> hashes = new LinkedList<>();
        String password = getRandomPassword();
        //passwords.add(password);
        HashCode hash = getHash(password);
        //hashes.add(hash);
        for (int i = 0; i < CHAIN_LENGTH; i++) {
            String pass = getReductionPassword(hash.asBytes(), i);
            //passwords.add(pass);
            hash = getHash(pass);
            //hashes.add(hash);
        }
        //Set<String> uniquePasswords = new HashSet<>(passwords);
        db.addToDatabase(password, hash.toString());
    }

    void createChain(String head) throws IOException {
        reductions = db.getReductions();
        //List<String> passwords = new LinkedList<>();
        //List<HashCode> hashes = new LinkedList<>();
        FileWriter writer = new FileWriter("chain.txt", false);
//            for (String hash : hashList) {
//                writer.write(hash + " " + passwords.get(hash) + '\n');
//            }
//            writer.flush();
        String password = head;
        writer.write(password + '\n');
        HashCode hash = getHash(password);
        writer.write(hash.toString()  + '\n');
        //hashes.add(hash);
        for (int i = 0; i < CHAIN_LENGTH; i++) {
            String pass = getReductionPassword(hash.asBytes(), i);
            writer.write(pass + '\n');
            hash = getHash(pass);
            writer.write(hash.toString()  + '\n');
        }
        writer.flush();
        //Set<String> uniquePasswords = new HashSet<>(passwords);
        //db.addToDatabase(password, hash.toString());
    }

    void createTable(){
        createReductionFunctions();
        for (int i = 0; i < CHAIN_NUMBER; i++) {
            if(i % 10000000 == 0){
                System.out.println(i);
            }
            createChain();
        }
        finish();
    }


    void finish() {

        db.finishWorkWithDb();
    }

    public static void main(String[] args) {
        RainbowTableCreator creator = new RainbowTableCreator();
        //creator.createTable();
        try {
            creator.createChain("qyuutee}io");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
