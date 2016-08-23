import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class serverMain {
    //variables and objects
    ServerSocket server;
    Socket socket;
    ArrayList writerList;
    ArrayList onlineUsers;
    ArrayList storedMessages;
    PrintWriter writer;
    String message;
    BufferedReader reader;

    public void start(){
        //init lists
        writerList = new ArrayList();
        onlineUsers = new ArrayList();
        storedMessages = new ArrayList();

        //init server
        try {
            server = new ServerSocket(9999);
            //wait for connections
            while (true){
                socket = server.accept();
                //get printwriter and store it in list
                writer = new PrintWriter(socket.getOutputStream());
                writerList.add(writer);
                //start thread for handling messages
                Thread thread = new Thread(new ClientHandler(socket));
                thread.start();
                System.out.println("new client connected");

            }
        }catch (Exception ex){ex.printStackTrace();}




    }
    // threading class for managing clients and reading their messages
    public class ClientHandler implements Runnable {
        //constructor
        public ClientHandler(Socket socket){
            //init bufferedreader
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }catch (Exception ex){ex.printStackTrace();}
        }
        public void run(){
            //read incoming messages
            try {
                while ((message = reader.readLine())!= null){
                    //handle messages
                    System.out.println(message);
                    handleMessage(message);


                }
            }catch (Exception ex){ex.printStackTrace();}
        }
    }


    /////////////////////////FUNCTION FOR HANDLING MESSAGES/////////////////////////////////////
    public void handleMessage(String message){


        //////////////new client connected message///////////////////
        if (message.contains("<%$login%$>")){
            //delete the code to get the account name
            String user = message.replace("<%$login%$>","");
            //store account name in the arraylist of online users
            onlineUsers.add(user);
            //set user online
            serverDBHandler handler = new serverDBHandler();


            //inform all the users friends to update their friend list
            ArrayList friendlist = handler.getFriends(user);
            int counter = 0;
            Iterator it1 = friendlist.iterator();
            Iterator it2 = onlineUsers.iterator();
            while (it2.hasNext()){
                System.out.println(it2.next().toString());
            }

            for (int i = 0; i < onlineUsers.size(); i++){
                for (int j = 0; j < friendlist.size(); j++){
                    if (friendlist.get(j).toString().equals(onlineUsers.get(i).toString())){
                        PrintWriter wrt = (PrintWriter) writerList.get(i);
                        wrt.println("<%$update%$>");
                        wrt.flush();
                    }
                }
            }
            //close connection
            handler.closeConnection();
        }

       /////////// //handle disconnect message and set the user offline////////////////////////////
        //then get the friendlist and send update message to all online friends
        else if(message.contains("<%$disconnect%§>")){
            String user = message.replace("<%$disconnect%§>","");
            serverDBHandler handler = new serverDBHandler();


            ArrayList friends = handler.getFriends(user);
            //search for online friends and also delete the disconnecting user from the list
            for (int i = 0; i < onlineUsers.size(); i++){
                for (int j = 0; j < friends.size(); j++){
                    if (friends.get(j).toString().equals(onlineUsers.get(i).toString())){
                        //we found online friend, send him update message
                        PrintWriter wrt = (PrintWriter) writerList.get(i);
                        wrt.println("<%$update%$>");
                        wrt.flush();
                    }
                }
                if (onlineUsers.get(i).toString().equals(user)){
                    //we found the disconnecting user. Delete the user from online users list and its print writer
                    onlineUsers.remove(i);
                    writerList.remove(i);
                }
            }
            //finally close the connection
            handler.closeConnection();

        }

        //handling delete friend message
        else if(message.contains("<%$deleted%$>")){
            //send message to the user who got deleted to update their list
            String user = message.replace("<%$deleted%$>","");
            for (int i = 0; i < onlineUsers.size(); i++){
                if (onlineUsers.get(i).toString().equals(user)){
                    //user found, send message via print writer
                    PrintWriter wrt = (PrintWriter) writerList.get(i);
                    wrt.println("<%$update%$>");
                    wrt.flush();
                }
            }
        }



        ////////////////////message from client to another client///////////////////////////
        else if (message.contains("<$%msg%§>")){
            String temp = message.replace("<$%msg%§>","");
            String[] users = message.split(",");
            //search for the online users to get the printwriter of the chartpartner we wanna send the message to
            for (int i = 0; i < onlineUsers.size(); i++){
                if (onlineUsers.get(i).equals(users[1])){
                    //we found the user, init printwriter
                    PrintWriter wrt = (PrintWriter) writerList.get(i);
                    wrt.println(message);
                    wrt.flush();
                }
            }


        }



        ///////////////handling friend request////////////////////////////
        else if(message.contains("<%$add%§>")){
            //search if the user is online
            String temp = message.replace("<%$add%§>","");
            String[] users = temp.split(",");
            boolean found = false;


            Iterator it = onlineUsers.iterator();
            int counter = 0;
            System.out.println("User 0: "+ users[0]+ " User 1: "+ users[1]);

            //look if the friend requester is still online
            for (int i = 0; i < onlineUsers.size(); i++){
                if (onlineUsers.get(i).equals(users[0])){
                    //he is online so add true
                    message +=",<%$true$%>";
                }
            }




            while (it.hasNext()){
                if (it.next().toString().equals(users[1])){
                    //the user is online, so send him the message that someone wants to add him as a friend
                    found = true;
                    PrintWriter wrt = (PrintWriter) writerList.get(counter);
                    wrt.println(message);
                    wrt.flush();
                }
                counter+=1;
            }
            if (!found){
                //user is not online, so save the message in an arraylist for later
                storedMessages.add(message);

            }

        }

        //handling message that the user accepted the friendrequest
        else if (message.contains("<%$friendaccepted%§>")){
            String user = message.replace("<%$friendaccepted%§>","");
            //init iterator and look if the user is online, then send message to him to update their friend list
            Iterator it =onlineUsers.iterator();
            message = "<%$update%$><image>";
            //the friend requester has a new friend so add image to the update message
            int counter = 0;
            for (int i = 0; i < onlineUsers.size(); i++){
                if (onlineUsers.get(i).toString().equals(user)){
                    //we found the user now send him the message
                    PrintWriter wrt = (PrintWriter) writerList.get(i);
                    wrt.println(message);
                    wrt.flush();
                }
            }
        }



    }





    public static void main(String[]args){
        //start server
        serverMain server = new serverMain();
        System.out.println("server started");
        server.start();


    }



}
