import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javafx.scene.image.Image;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class serverDBHandler {
    //database information
    private final String userID = "root";
    private final String password = "vegeta1991";
    private final int port = 3306;
    private final String serverName = "localhost";
    private final String dataBase = "messenger";
    //variables
    MysqlDataSource dataSource;
    Connection connection;
    Statement statement;

    //ArrayLists


    public serverDBHandler(){
        //init database connection
        //datasource
        dataSource = new MysqlDataSource();
        dataSource.setDatabaseName(dataBase);
        dataSource.setServerName(serverName);
        dataSource.setPort(port);

        //connection
        try {
            connection = dataSource.getConnection(userID,password);
        }catch (Exception ex) {ex.printStackTrace();}



    }

    //closes the connection
    public void closeConnection(){
        try{
            connection.close();
        }catch (SQLException ex){ex.printStackTrace();}

    }

    //get friends for login and logout function
    public ArrayList getFriends(String user) {
        ArrayList friends = new ArrayList();
        try {
            statement = connection.createStatement();
            //execute SQL query and get the data from the database
            ResultSet myResult = statement.executeQuery("SELECT * FROM " + user);
            //add the friends to the list
            while (myResult.next()) {
                if (!myResult.getString("friend").equals(""))
                     friends.add(myResult.getString("friend"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return friends;
    }


}
