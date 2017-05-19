import java.sql.*;
import java.util.Scanner;

/**
 * Created by Edu on 18/5/17.
 */
public class Main {
    static Scanner scanner = new Scanner(System.in); //Sirve para recoger texto por consola
    static int select = -1; //opción elegida del usuario
    //static int num1 = 0, num2 = 0; //Variables
    static String player;

    public static void main(String[] args) {

        //Mientras la opción elegida sea 0, preguntamos al usuario
        while(select != 5){
            //Try catch para evitar que el programa termine si hay un error
            try{
                int select = printMenu();

                //Ejemplo de switch case en Java
                switch(select){
                    case 1:
                        createPlayer();
                        break;
                    case 2:
                        showTable();
                        break;
                    case 3:
                        searchPlayerById();
                        break;
                    case 4:
                        deletePlayerById();
                        break;
                    case 5:
                        System.out.println("Bye!");
                        break;
                    default:
                        System.out.println("Unknown option");
                        break;
                }

                System.out.println("\n");

            }catch(Exception e){
                System.out.println("Uoop! Error!");
            }
        }
    }

    public static int printMenu(){

        System.out.println("Choose option: \n" +
                "1.- Create player \n" +
                "2.- Show table \n" +
                "3.- Search Player by ID \n" +
                "4.- Delete Player by ID\n" +
                "5.- Salir");

        // returns catching variable in console
        return Integer.parseInt(scanner.nextLine());
    }

    // OPTION 1
    public static void createPlayer(){
        try {
            System.out.print("Player name: ");
            String nombre = scanner.nextLine();
            System.out.print("Player lastname: ");
            String apellido1 = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Player email: ");
            String email = scanner.nextLine();
            System.out.println("\n");

            Connection c = Singleton.getConnection();
            StringBuilder query =  new  StringBuilder("SELECT id FROM Players ORDER BY id DESC LIMIT 1");

            PreparedStatement st;
            st = c.prepareStatement(query.toString());

            // execute select SQL stetement
            ResultSet rs = st.executeQuery();
            // moving the cursor to the first row
            rs.next();
            Integer playerId = rs.getInt(1) + 1;

            st = c.prepareStatement("INSERT INTO Players (id, nombre, apellido1, apellido2, password, email) VALUES (?, ?, ?, NULL, ?, ?)");
            st.setInt(1, playerId);
            st.setString(2, nombre);
            st.setString(3, apellido1);
            st.setString(4, password);
            st.setString(5, email);

            if(st.executeUpdate()==1){
                System.out.println("PLAYER " + nombre + " " + apellido1 +" INSERTED");
            }


        }
        catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    // OPTION 2
    public static void showTable(){
        try {
            System.out.print("Write the table you want to show: ");
            String tableName = scanner.nextLine();
            System.out.println("\n");
            Connection c = Singleton.getConnection();
            StringBuilder query =  new  StringBuilder("SELECT * FROM ");
            query.append(tableName);

            PreparedStatement st;
            st = c.prepareStatement(query.toString());

            // execute select SQL stetement
            ResultSet rs = st.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for(int i = 1; i < columnsNumber; i++) {
                    System.out.print(rsmd.getColumnName(i) + "-> " +rs.getString(i) + "\t\t\t" +
                            "");
                }
                System.out.println();
            }
            /* ANOTHER SOLUTION TO SHOW THE ROWS
              while (rs.next()) {
                Integer id = rs.getInt("ID");
                String name = rs.getString("NOMBRE");
                String apellido1 = rs.getString("APELLIDO1");
                String apellido2 = rs.getString("APELLIDO2");
                String password = rs.getString("PASSWORD");
                String email = rs.getString("EMAIL");
                System.out.println(id + "\t" + name + "\t" + apellido1 + "\t" + apellido2 + "\t" + password + "\t" + email);
            }*/
        }
        catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    // OPTION 3
    public static void searchPlayerById(){
        try {
            System.out.println("Write the player's ID you want to search:");
            int playerId = Integer.parseInt(scanner.nextLine());
            System.out.println("\n");

            Connection c = Singleton.getConnection();
            PreparedStatement st;
            st = c.prepareStatement("SELECT * FROM Players WHERE id = ?");
            st.setInt(1, playerId);

            // execute select SQL stetement
            ResultSet rs = st.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for(int i = 1; i < columnsNumber; i++) {
                    System.out.print(rsmd.getColumnName(i) + "-> " +rs.getString(i) + "\t\t\t" +
                            "");
                }
                System.out.println();
            }
        }
        catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    // OPTION 4
    public static void deletePlayerById(){
        try {
            System.out.print("Write the player's ID you want to delete: ");
            int playerId = Integer.parseInt(scanner.nextLine());
            System.out.println("\n");

            Connection c = Singleton.getConnection();
            PreparedStatement st1;
            //  we delete the player
            st1 = c.prepareStatement("DELETE FROM Players WHERE id = ?");
            st1.setInt(1, playerId);

            if(st1.executeUpdate() == 1){
                System.out.println("Player deleted");
            }
            // I can control this using a DB trigger after delete but for practise I control that here- if exists the player in playergames we delete the player
            PreparedStatement st2;
            st2 = c.prepareStatement("SELECT COUNT(*) FROM PlayersGames WHERE Players_id = ?");
            st2.setInt(1, playerId);

            ResultSet rs = st2.executeQuery();
            rs.next();
            Integer playerGames = rs.getInt(1);

            if(playerGames != 0){
                PreparedStatement st3;
                st3 = c.prepareStatement("DELETE FROM PlayersGames WHERE Players_id = ?");
                st3.setInt(1, playerId);
                st3.executeUpdate();
            }

            if(st1.executeUpdate() == 1){
                System.out.println("Player deleted");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
