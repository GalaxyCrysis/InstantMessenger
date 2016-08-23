
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class networkHandler {
    //objects
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    String message;
    Thread thread;
    Messenger messenger;
    //connect to sever
    public networkHandler(Messenger messenger){
        this.messenger = messenger;
        try {
            socket = new Socket("192.168.178.21",9999);
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //thread for incoming messages
            thread = new Thread(new messageHandler());
            thread.start();


        }catch (Exception ex){ex.printStackTrace();}
    }
    //send message to server
    public void writeMessage(String message){
        writer.println(message);
        writer.flush();
    }


    //a function to handle messages from the server
    public void handleMessage(String message){



        //handling friend request message
        if (message.contains("<%$add%§>")){
            message = message.replace("<%$add%§>","");
            String[] users = message.split(",");
            //init friendAdder and open dialog
            FriendHandler adder = new FriendHandler();
            boolean answer = adder.addFriend(users[0]);
            if (answer){
                //we accepted the friend request! Add the new friend to our friend list in the database
                dbHandler handler = new dbHandler();
                //add both to their counterparts tables
                if (message.contains("<%$true$%>"))
                    handler.addFriend(users[0],users[1],true);
                else
                    handler.addFriend(users[0],users[1],false);

                handler.addFriend(users[1],users[0],true);
                //send message to the server that we accepted, then the server
                // will send the message back to the friend requester
                String msg = "<%$friendaccepted%§>" + users[0];
                writeMessage(msg);

                //update our friendlist
                if (message.contains("image"))
                    messenger.updateList(true);
                else
                    messenger.updateList(false);

            }

        }
        //handle update list message
       else if (message.contains("<%$update%$>")){
            //update our friendlist
            messenger.updateList(false);

        }
        //handle messages from chatpartner
        else if(message.contains("<$%msg%§>")){
            message = message.replace("<$%msg%§>","");
            String[] messagesplit = message.split(",");
            String msg =  messenger.getDate() + " " + messagesplit[0] + ":" + messagesplit[2] + "\n";
            //update the message list of that friend
            messenger.updateMessageList(messagesplit[0],msg);
            //check if the friend is our current chatpartner
            if (messenger.getChatpartner()!=""){
                if (messenger.getChatpartner().equals(messagesplit[0])){
                    //update conversation area
                    messenger.updateConversationArea();
                }
            }

            else{
                //open a notification that our friend sent us a message
                messenger.notification(messagesplit[0]);

            }

        }


    }
    //close connection
    public void disconnect(){
        try {
            socket.close();
            writer.close();
            reader.close();
            thread.interrupt();
        }catch (Exception ex){ex.printStackTrace();}
    }




    //class for reading messages from server
    public class messageHandler implements Runnable{
        public void run(){
            try {
                //read message
                while((message = reader.readLine())!= null){
                    Platform.runLater(()->handleMessage(message));

                }
            }catch (Exception ex){ex.printStackTrace();}
        }
    }

}
