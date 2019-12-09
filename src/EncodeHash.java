import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EncodeHash extends HashEngine{
    private static DbWorker db = new DbWorker();

   private String reductionAndHash(String hash, int countSteps){
        String otherHash = hash;
        for (int i = countSteps; i < CHAIN_LENGTH; i++) {
            String pass = getReductionPassword(getBytes(otherHash), i);
            otherHash = getHash(pass).toString();
        }
        return otherHash;
    }

    private String hashAndReduction(String password, int countSteps){
        for (int i = 0; i < countSteps; i++) {
            password = getReductionPassword(getHash(password).asBytes(), i);
        }
        return password;
    }

    public String encode(String hash){
        String headChain = db.findHash(hash);
        String hashReduct;
        String password;
        int i;
        for (i = 0; i < CHAIN_LENGTH; i++) {
            if (headChain != null){
                password = hashAndReduction(headChain, CHAIN_LENGTH - i);
                if(hash.equals(getHash(password).toString())){
                    return password;
                }
            }
            hashReduct = reductionAndHash(hash, CHAIN_LENGTH - 1 - i);
                headChain = db.findHash(hashReduct);
        }
            return "Cant find";
    }

   public ConcurrentMap<String, String> encode(List<String> hashList){
        reductions = db.getReductions();
        return hashList.stream().collect(Collectors.toConcurrentMap(Function.identity(), s ->encode(s)));
    }

   private byte [] getBytes(String hash){
        byte[] bytes = new byte[hash.length()/2];
        for (int i = 0; i < hash.length()/2; i++) {
            int numb = Integer.parseInt(hash.substring(i*2, i*2 + 2), 16);
            bytes[i] = (byte) numb;
        }
        return bytes;
    }

    public static void main(String[] args) {
      EncodeHash en = new EncodeHash();
        try {
            List<String> hashList = new ArrayList<>();
            String [] rows = new Scanner(new File("hashes.txt")).useDelimiter("\\Z").next().split("\r\n");
            Collections.addAll(hashList, rows);
            HashMap<String, String> passwords = new HashMap<>(en.encode(hashList));
            FileWriter writer = new FileWriter("decoded.txt", false);
            for (String hash : hashList) {
                writer.write(hash + " " + passwords.get(hash) + '\n');
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
