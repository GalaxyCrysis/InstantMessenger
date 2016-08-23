import com.mysql.jdbc.Blob;
import com.mysql.jdbc.StreamingNotifiable;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.scene.image.WritableImage;
import org.mindrot.BCrypt;

import javax.imageio.ImageIO;


public class dbHandler {
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
    ArrayList nameList;
    ArrayList imageList;
    ArrayList friendlist;


    //constructor
    public dbHandler(){
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

        //init lists
        nameList = new ArrayList();
        imageList = new ArrayList();
        friendlist = new ArrayList();

    }
    //function for login. we use the name for sql query and look if password and the stored hashed password
    //are equal
    public String login(String name, String password){
        String message ="";
        boolean password_verified = false;
        try {
            statement = connection.createStatement();
            //execute SQL query and get the data from the database
            ResultSet myResult = statement.executeQuery("SELECT name,password FROM members WHERE name = '" + name +"'");
            while (myResult.next()) {
                //compare password with hashed password
                String stored_hash = myResult.getString("password");
                password_verified = BCrypt.checkpw(password, stored_hash);
            }
            if(password_verified)
                message ="success";
            else
                message="Account name or Password wrong!";


        }catch (SQLException ex){
            ex.printStackTrace();}


        return message;
    }
    //changes the password in the database
    public void changePassword(String account_name, String newPassword){
        //hashe the password
        newPassword = hashPassword(newPassword);
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE members SET password='"+newPassword+"' WHERE" +
                    " name='"+account_name+"'");
        }catch (SQLException ex){ex.printStackTrace();}
    }


    //look if user exists
    public boolean userExists(String user){
        boolean answer = false;
        //look into the database
        try{
            statement = connection.createStatement();
            //execute SQL query and get the data from the database
            ResultSet myResult = statement.executeQuery("SELECT name FROM members WHERE name = '" + user +"'");
            while (myResult.next()) {
                if (myResult.getString("name")!= null)
                    //name exists so answer = true
                    answer = true;
            }

        }catch (SQLException ex){ex.printStackTrace();}
        return answer;
    }


    //////////////////////function for adding a friend/////////////////////////////
    public void addFriend(String friendName, String user, boolean online){
        try {
            statement = connection.createStatement();
            //add the friend in the database table of the user
            if (online){
                statement.executeUpdate("INSERT INTO " + user + "(friend,online) VALUES('" + friendName +
                        "','online')");
            }
            else {
                statement.executeUpdate("INSERT INTO " + user + "(friend,online) VALUES('" + friendName +
                        "','offline')");
            }

            statement.close();
        }catch (SQLException ex){ex.printStackTrace();}
    }




    ///////////////////////////REGISTER/////////////////////////////////
    public String register(String name, String email, String password, byte[]bytes){
        String message ="";
        boolean used = false;
        //we gonna select email and password from the database. if there is an error, then password
        // or email are not in use
        try {
            statement = connection.createStatement();
            //execute SQL query and get the data from the database
            ResultSet myResult = statement.executeQuery("SELECT * FROM members WHERE name = '" + name +"'");
            while (myResult.next()){
                if(myResult.getString("name")!= null){
                    //name already in use
                    message +="name is already in use! \n";
                    used = true;

                }

            }
            myResult.close();
            statement.close();

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        try {
            statement = connection.createStatement();
            //execute SQL query and get the data from the database
            ResultSet myResult = statement.executeQuery("SELECT * FROM members WHERE email = '" + email +"'");
            while (myResult.next()){
                if(myResult.getString("email")!=null){
                    //email already in use
                    used = true;
                    message+="email already in use!\n";

                }
            }
            myResult.close();
            statement.close();

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        //register if neither email and name are used
        if (!used && !message.contains("already in use")){
            message ="success";
            //register
            try {
                //query
                String query ="INSERT INTO members(name,email,password,image) VALUES(?,?,?,?)";
                statement = connection.createStatement();
                //hash the password
                password = hashPassword(password);
                //execute sql query and insert the data into the database
                //add user to members table
                PreparedStatement stmnt = connection.prepareStatement(query);
                stmnt.setString(1,name);
                stmnt.setString(2,email);
                stmnt.setString(3,password);
                stmnt.setBytes(4,bytes);
                stmnt.execute();



                //create friendlist for the new user
                String sql = "CREATE TABLE "+ name + "(id INT NOT NULL AUTO_INCREMENT, friend VARCHAR(32),  " +
                        "online VARCHAR(32)," +
                        "PRIMARY KEY(id))";
                statement.execute(sql);
                statement.close();



            }catch (SQLException ex){
                ex.printStackTrace();
            }

        }


        //return the message to registerlayout
        return message;
    }

    //aa function for getting the images of the friends
    public ArrayList getFriendData(String account_name, String data){
        ArrayList friendList = new ArrayList();
        ArrayList imageList = new ArrayList();
        ArrayList friends = new ArrayList();
        ArrayList onlinefriends = new ArrayList();
        ArrayList offlineFriends = new ArrayList();

       try{
           statement = connection.createStatement();



               //execute SQL query and get the data from the database
               ResultSet myResult = statement.executeQuery("SELECT * FROM " + account_name);
               //add the friends and online status to the lists
               while (myResult.next()){
                   friends.add(myResult.getString("friend"));
                   if (myResult.getString("online").equals("online"))
                       onlinefriends.add(myResult.getString("friend"));
                   else
                       offlineFriends.add(myResult.getString("friend"));

               }


               //now get the images of the friends from members table in a for loop

               for (int i = 0; i < friends.size();i++){
                   myResult = statement.executeQuery("SELECT image,name FROM members WHERE name ='" +friends.get(i) + "'" );
                   while (myResult.next()){
                       //get bytes from database for image

                       friendList.add(myResult.getString("name"));
                       //get the blob
                       byte[]bytes = myResult.getBytes("image");

                       try {
                           //convert it to an fximage
                           ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                           BufferedImage bufferedImage = ImageIO.read(in);
                           Image image = SwingFXUtils.toFXImage(bufferedImage,null);


                           imageList.add(image);

                       }catch (IOException ex){ex.printStackTrace();}



                   }
               }



       }catch (SQLException ex){ex.printStackTrace();}
        if (data.equals("image"))
           return imageList;
        else if (data.equals("friends"))
            return friendList;
        else if(data.equals("offline"))
            return offlineFriends;
        else
            return onlinefriends;



    }

    //a function for verifying that the friend exists in the friend list of the user(account_name)
    public boolean friendExists(String account_name, String friend){
        boolean answer = false;
        try {
            statement = connection.createStatement();
            ResultSet myResult = statement.executeQuery("SELECT * FROM "+account_name+" WHERE friend='"
            +friend+"'");
            while (myResult.next()){
                if (myResult.getString("friend").equals(friend)){
                    //friend exists, so set answer true
                    answer = true;
                }
            }
        }catch (SQLException ex) {ex.printStackTrace();}
        return answer;
    }

    //we will get all friends from the user and set user online/offline in their lists
    public void changeStatus(String user, String method){
        ArrayList friends = new ArrayList();
        try {
            statement = connection.createStatement();
            //execute SQL query and get the data from the database
            ResultSet myResult = statement.executeQuery("SELECT * FROM " + user);
            //add the friends to the list
            while (myResult.next()){
                friends.add(myResult.getString("friend"));
            }

            //now set the user online/offline in all the friends lists
            Iterator it = friends.iterator();
            while (it.hasNext()){
                if (method.equals("online")){
                    statement.executeUpdate("UPDATE " + it.next().toString() + " SET online = 'online' WHERE friend ='"+ user +" '");
                }
                else {
                    statement.executeUpdate("UPDATE " + it.next().toString() + " SET online = 'offline' WHERE friend ='"+ user +" '");
                }

            }



        }catch (SQLException ex){ex.printStackTrace();}

    }

    //a function for deleting a friend from friend list and the user from the friends friendlist
    public void deleteFriend(String user, String friend){
        try {
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM "+ user + " WHERE friend='" + friend +"'");
            statement.executeUpdate("DELETE FROM "+friend + " WHERE friend='"+user+"'");
        }catch (SQLException ex){ex.printStackTrace();}

    }
    //closes the connection
    public void closeConnection(){
        try{
            connection.close();
        }catch (SQLException ex){ex.printStackTrace();}

    }



    //function for hashing passwords
    public static String hashPassword(String password){
        String salt = BCrypt.gensalt(12);
        String hashed_password = BCrypt.hashpw(password,salt);
        return hashed_password;
    }




}
