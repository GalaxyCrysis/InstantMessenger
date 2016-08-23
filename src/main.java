import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;


public class main extends Application {
    //set variables
    Stage window;
    String title = "Messenger";
    Label failLabel;
    Scene loginScene;
    TextField loginName;
    PasswordField loginPassword;
    String message;
    String account_name;



    public void start(Stage primarystage) throws Exception{
        //init window
        window = primarystage;
        //login layout

        //login button
        Button loginButton = new Button();
        loginButton.setText("Login");
        loginButton.setOnAction(e -> login());

        //register button
        Button registerButton = new Button();
        registerButton.setText("Register");
        registerButton.setOnAction(e-> register());

        //login textfields
        loginName = new TextField();
        loginName.setMaxWidth(150);
        loginName.setText("galaxy");
        loginPassword = new PasswordField();
        loginPassword.setMaxWidth(150);
        loginPassword.setText("vegeta1991");


        //label
        Label information = new Label();
        information.setText("register if you don't have an account yet");

        failLabel = new Label();
        failLabel.setText("");
        failLabel.setId("fail-label");

        Label name = new Label();
        name.setText("Account:");

        Label password = new Label();
        password.setText("Password:");

        Label confirmpassword = new Label();
        confirmpassword.setText("Confirm password");

        /////////////////////LAYOUT///////////////////
        //account and password Vboxes
        HBox namebox = new HBox();
        namebox.getChildren().addAll(name,loginName);
        namebox.setAlignment(Pos.CENTER);
        namebox.setMinWidth(150);

        HBox passwordbox = new HBox();
        passwordbox.getChildren().addAll(password,loginPassword);
        passwordbox.setAlignment(Pos.CENTER);
        passwordbox.setMinWidth(200);


        HBox buttonbox = new HBox();
        buttonbox.getChildren().addAll(loginButton,registerButton);
        buttonbox.setAlignment(Pos.CENTER);
        buttonbox.setMinWidth(200);
        buttonbox.setSpacing(10);

        //main vbox
        VBox mainbox = new VBox();
        mainbox.getChildren().addAll(namebox,passwordbox,buttonbox,failLabel,information);
        mainbox.setAlignment(Pos.CENTER);
        mainbox.setSpacing(30);

        //init scene
        loginScene = new Scene(mainbox,350,350);
        loginScene.getStylesheets().add("style.css");

        window.setTitle(title);
        window.setMinWidth(450);
        window.setMinHeight(350);
        window.setScene(loginScene);
        window.show();


    }

    //login
    public void login(){
        String message="";
        //init dbHandler
        dbHandler handler = new dbHandler();
        message = handler.login(loginName.getText(),loginPassword.getText());
        if (!message.contains("success"))
            failLabel.setText(message);
        else {
            //we have successfully authenticated, now init network handler and connect to the server
            //we start the msssenger gui. The main class acts as controller, while messenger gui is the view
            // and the network class is the model
            try {

                Messenger messenger = new Messenger(loginName.getText());
                account_name = loginName.getText();
                networkHandler netHandler = new networkHandler(messenger);
                //send name to the server
                String code = "<%$login%$>" + loginName.getText();
                netHandler.writeMessage(code);
                //change status
                handler.changeStatus(account_name,"online");
                handler.closeConnection();


                //handle key pressed event. We get the text from the textarea and send the text via
                //network handler to the server
                messenger.getMessageText().setOnKeyPressed(e->{
                    if(e.getCode() == KeyCode.ENTER){
                        //send message if we have a text partner
                        if (!messenger.getTextPartnerBoolean()) {

                            e.consume();
                            String friendname = messenger.getFriendName();
                            String msg = "<$%msg%§>" + account_name + "," + friendname + "," +
                                    messenger.getMessageText().getText();
                            netHandler.writeMessage(msg);


                            //add message to the conversation area
                            String date = messenger.getDate();
                            //update messagelist
                            messenger.updateMessageList(friendname,date + " " + account_name + ":" +
                            messenger.getMessageText().getText() + "\n");
                            messenger.updateConversationArea();
                            //clear the textbox
                            messenger.getMessageText().setText("");
                        }
                    }

                });
                //change password menu item click event. Databasehandler will compare old password
                //with inserted password and then will replace it with new password
                messenger.getChangePassword().setOnAction(e->{
                    FriendHandler adder = new FriendHandler();
                    boolean answer = adder.changePassword(account_name);
                    if (answer){
                        //changing password was successful. SHow messagebox
                        JOptionPane.showMessageDialog(null, "You changed your password!","New Password",JOptionPane.INFORMATION_MESSAGE);

                    }
                });




                //disconneect menu item action event
                //and send a message to the server. The server will send a message to all the
                //online friends to update their lists
                //finally the server will update the database that the user is offline

                messenger.getDisconnect().setOnAction(e->{
                    String disconnectMessage = "<%$disconnect%§>" + account_name;
                    netHandler.writeMessage(disconnectMessage);
                    netHandler.disconnect();
                    //set the login scene
                    window.setScene(loginScene);

                    //init dbhandler and set the user offline
                    dbHandler DBHandler = new dbHandler();
                    DBHandler.changeStatus(account_name,"offline");
                    DBHandler.closeConnection();


                });

                //quit menu item clicked event, the same as disconnect but the programm will close
                messenger.getQuit().setOnAction(e->{
                    String disconnectMessage = "<%$disconnect%§>" + account_name;
                    netHandler.writeMessage(disconnectMessage);
                    netHandler.disconnect();
                    //set the login scene


                    //init dbhandler and set the user offline
                    dbHandler DBHandler = new dbHandler();
                    DBHandler.changeStatus(account_name,"offline");
                    DBHandler.closeConnection();

                    //close program
                    Platform.exit();
                });

                //delete friend menu item action event
                //open a dialog where the user can insert the friend he wanna delete
                //and returns the name. Then the user will send the server the message that he deleted
                // a friend. The server will search for the friend in the onlineusers list and send him
                //a message to update his list
                //finally the databasehandler will delete the user in the database of the deleted friend and vice versa
                messenger.getDeleteFriend().setOnAction(e->{
                    //init dialog window
                    FriendHandler adder = new FriendHandler();
                    String friend = adder.deleteFriend(account_name);
                    if (!friend.equals("")){
                        //init database handler
                        dbHandler DBhandler = new dbHandler();
                        DBhandler.deleteFriend(account_name,friend);
                        String msg = "<%$deleted%$>"+friend;
                        //send message to server
                        netHandler.writeMessage(msg);
                        //close database connection
                        DBhandler.closeConnection();

                        //update list
                        messenger.updateList(true);
                    }



                });

                //add a friend on menu click event handling
                //if we get a name we send the name to the server to add him as a new friend
                messenger.getAddFriendMenuItem().setOnAction(e->{

                    String name = messenger.addFriend();
                    if(!name.equals("")){
                        //we got a name so the user exists in the database
                        //send the message to the server
                        String msg = "<%$add%§>" + account_name + ","+ name;
                        System.out.println(msg);
                        netHandler.writeMessage(msg);
                    }
                });

                Scene scene = messenger.getScene();
                scene.getStylesheets().add("style.css");
                window.setScene(scene);

            }catch (Exception ex){
                //couldnt connect to server
                failLabel.setText("cannot connect to server");
            }

        }

    }

    //open register dialog for registration
    public void register(){
        RegisterLayout register = new RegisterLayout();
        register.display();

    }


    public static void main(String[] args) {

        launch(args);
    }


}
