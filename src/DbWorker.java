import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DbWorker {
    private Connection conn;
    private HashMap<Character, PreparedStatement> insertors = new HashMap<>();
    private HashMap<Character, PreparedStatement> selectors = new HashMap<>();
    private final static Character[] alphabet16 = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    DbWorker(){
        String myUrl = "jdbc:oracle:oci:@//localhost:1521/orcl";
        try {
            conn = DriverManager.getConnection(myUrl, "LERA", "LERA6666");
            String querySelect = "select head_ from RAINBOW_%c where tail_= ?";
            String queryInsert = " insert into RAINBOW_%c"  + " values (?, ?)";
            for(Character ch: alphabet16){
                insertors.put(ch, conn.prepareStatement(String.format(queryInsert, ch)));
                selectors.put(ch, conn.prepareStatement(String.format(querySelect, ch)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToDatabase(String password, String hash){
        try {
            PreparedStatement preparedStmt = insertors.get(hash.charAt(0));
            preparedStmt.setString(1, password);
            preparedStmt.setString(2, hash);
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToDatabase(List<List<Integer>> reductions){
        try {
            String queryInsert = " insert into reductions"  + " values (?, ?)";
            PreparedStatement preparedStmtReduction = conn.prepareStatement(queryInsert);
            for (int i = 0; i < reductions.size(); i++) {
                preparedStmtReduction.setInt(1, i);
                preparedStmtReduction.setString(2, reductions.get(i).toString().replaceAll("[\\[\\]]", ""));
                preparedStmtReduction.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<List<Integer>> getReductions(){
        List<List<Integer>> reductions = new ArrayList<>();
        try {
            String selectQuery = "select REDUCTION from REDUCTIONS order by serial_number";
            PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
            ResultSet rs = preparedStmt.executeQuery();
            while(rs.next()) {
                List<Integer> reduction = Arrays.stream(rs.getString(1).split(", "))
                        .map(Integer::parseInt).collect(Collectors.toList());
                reductions.add(reduction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reductions;
    }

    /**
    @return password
    */
    public String findHash(String hash){
        try {
            PreparedStatement preparedStmt = selectors.get(hash.charAt(0));
            preparedStmt.setString(1, hash);
            ResultSet rs = preparedStmt.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void finishWorkWithDb(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
