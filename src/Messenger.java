


//main class for the messenger. It handels all the gui and acts as the view class

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Messenger {
    //objects
    ListView<String> friendList;
    ListView<Label> imageList;
    ArrayList friends;
    String[] messages;
    Button sendMessageButton;
    TextArea messageText;
    TextArea conversationArea;
    Label nameLabel;
    Label imageLabel;
    String accountName;
    MenuItem disconnect;
    MenuItem deleteFriend;
    MenuBar menubar;
    Label friendName;
    Label friendsOnline;
    MenuItem addFriendMenu;
    MenuItem changePassword;
    MenuItem quit;
    VBox layout;
    VBox leftBox;
    HBox bottom;
    TextField addFriendField;
    boolean textpartner = false;
    ArrayList images;
    ArrayList messageList;
    String chatpartner;



    public Messenger(String accountName){
        //get the accountname
        this.accountName = accountName;
        //array list for saved messages
        messageList = new ArrayList();
        chatpartner = "";

        //init Menu
        //main menu
        Menu messenger = new Menu("Messenger");
        Menu profil = new Menu("Profile");
        MenuItem changeImage = new MenuItem("Change Image");
        changePassword = new MenuItem("change Password...");
        disconnect = new MenuItem("Disconnect");
        quit = new MenuItem("Quit");

        profil.getItems().add(changeImage);
        messenger.getItems().addAll(profil,changePassword,disconnect,quit);

        //contacts menu
        Menu contacts = new Menu("Contacts");
        addFriendMenu = new MenuItem("Add a Friend");
        deleteFriend = new MenuItem("Delete a Friend");
        contacts.getItems().addAll(addFriendMenu,deleteFriend);



        menubar = new MenuBar();
        menubar.getMenus().addAll(messenger,contacts);

        //account name label
        nameLabel = new Label();
        nameLabel.setText(accountName);
        nameLabel.setId("bold-label");
        nameLabel.setAlignment(Pos.TOP_LEFT);

        //friendsonline
        friendsOnline = new Label();
        friendsOnline.setAlignment(Pos.BOTTOM_LEFT);
        //friendsname is a label for the chat partner we are currently chatting with
        friendName = new Label();
        friendName.setAlignment(Pos.TOP_RIGHT);
        friendName.setId("messenger-label");
        //image label of the friend
        imageLabel = new Label();

        //send message text field
        messageText = new TextArea();
        messageText.setMaxWidth(200);
        messageText.setMaxHeight(40);
        messageText.setWrapText(true);
        messageText.setId("text-box");


        //messagebox
        conversationArea = new TextArea();
        conversationArea.setMaxWidth(300);
        //conversationArea.setDisable(true);
        conversationArea.setWrapText(true);
        conversationArea.setId("text-box");
        conversationArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //when we have a new message the textarea will scroll to the bottom via Double.MAX_VALUE
                //only works with textarea.append(string)
                conversationArea.setScrollTop(Double.MAX_VALUE);
            }
        });
        //button
        sendMessageButton = new Button();
        sendMessageButton.setText("Send");
        sendMessageButton.setAlignment(Pos.CENTER);

        //list views
        friendList = new ListView<>();
        friendList.setMaxWidth(100);
        friendList.setMaxHeight(200);
        //set selected item event
        friendList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                chatpartner = newValue;
                friendName.setText(chatpartner);
                //get the image of the friend from images array list



                for (int i = 0; i < friends.size(); i++){
                    if (friends.get(i).toString().equals(chatpartner)){
                        Image image = (Image) images.get(i);
                        //init image view
                        ImageView view = new ImageView(image);
                        view.setFitWidth(100);
                        view.setFitHeight(100);
                        imageLabel.setGraphic(view);


                    }
                }
                updateConversationArea();
            }
        });
        imageList = new ListView<>();
        imageList.setMaxWidth(35);
        imageList.setMaxHeight(200);



        //Hbox for the lists
        HBox listBox = new HBox();
        listBox.getChildren().addAll(imageList,friendList);
        listBox.setAlignment(Pos.CENTER);

        //Hbox for friend name and image
        VBox friendBox = new VBox();
        friendBox.getChildren().addAll(imageLabel,friendName);
        friendBox.setSpacing(10);

        //Vbox for label and lists
        leftBox = new VBox();
        leftBox.getChildren().addAll(nameLabel,listBox,friendsOnline);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.setSpacing(10);


        VBox textarea = new VBox();
        textarea.getChildren().addAll(friendBox,conversationArea,messageText);
        textarea.setSpacing(20);
        textarea.setAlignment(Pos.CENTER);

        bottom = new HBox();
        bottom.getChildren().addAll(leftBox,textarea);
        bottom.setSpacing(40);



        //Vbox as main layout
        layout = new VBox();
        layout.getChildren().addAll(menubar,bottom);
        layout.setSpacing(40);

        //update list
        updateList(true);
        //init messagelist
        initMessageList();

    }
    //a function for updating the conversation box when we get a new message from a friend we are currently texting with
    public void updateConversationArea(){
        conversationArea.clear();
        for(int i = 0; i < friends.size(); i++){
            if (friends.get(i).toString().equals(chatpartner)){
                ArrayList list = (ArrayList) messageList.get(i);
                for (int j = 1; j < list.size(); j++){
                    conversationArea.setText(conversationArea.getText() + list.get(j).toString());
                }
            }
        }

    }
    //a function for initialising the messagelist. We create new array lists which save the messages for each friend
    // we have in the friend list and save this arraylist in the main array list messagelist
    public void initMessageList(){
        for(int i = 0; i < friends.size(); i++){
            ArrayList list = new ArrayList();
            list.add(friends.get(i).toString());
            messageList.add(list);
        }

    }
    //open up a new notification that a friend send the user a message
    public void notification(String friend){
        String message = friend + " sent you a new message!";
        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UTILITY);

        Label information = new Label();
        information.setText(message);
        information.setAlignment(Pos.CENTER);
        FlowPane pane = new FlowPane();
        pane.getChildren().add(information);
        Scene scene = new Scene(pane,50,50);
        dialogStage.setScene(scene);
        dialogStage.show();

    }

    //a function for updating the message list. When we get a new message from a friend we search for the
    //index of the friend and get access to the message list of that friend. Then we add the new message to the
    //array list and update the whole message list
    public void updateMessageList(String friend, String message){
        for(int i = 0; i < friends.size(); i++){
            if (friends.get(i).toString().equals(friend)){
                ArrayList list = (ArrayList) messageList.get(i);
                list.add(message);
                messageList.set(i,list);
            }
        }

    }
    //returns the chatpartner string
    public String getChatpartner(){
        return chatpartner;
    }
    //returns menu item quit
    public MenuItem getQuit(){
        return quit;
    }

    //returns the delete a friend MenuItem
    public MenuItem getDeleteFriend(){
        return deleteFriend;
    }

    //we open an input dialog and get the name of the user
    public String addFriend(){
        String name = "";
        FriendHandler adder = new FriendHandler();
        name = adder.getFriendName();

        return name;
    }
    //a function to update the friend list
    public void updateList(boolean getImages){
        //delete all items in the lists
        friendList.getItems().clear();
        imageList.getItems().clear();

        //get friends from database
        dbHandler handler = new dbHandler();
        friends = handler.getFriendData(accountName,"friends");
        ArrayList onlineFriends = handler.getFriendData(accountName,"online");
        ArrayList offlineFriends = handler.getFriendData(accountName,"offline");
        if (getImages){
            //getImage is true so get the friends images from the database
            images = handler.getFriendData(accountName,"image");
        }


        //init images
        Image offlineImage = new Image(getClass().getResourceAsStream("offline.png"));
        Image onlineImage = new Image(getClass().getResourceAsStream("online.png"));
        boolean online = false;
        //add online users to the lists
        for (int i = 0; i < onlineFriends.size(); i++){
            Label label = new Label();
            label.setGraphic(new ImageView(onlineImage));
            imageList.getItems().add(label);
            friendList.getItems().add(onlineFriends.get(i).toString());
        }
        //add offline users to the lists
        for(int i = 0; i < offlineFriends.size(); i++){
            Label label = new Label();
            label.setGraphic(new ImageView(offlineImage));
            imageList.getItems().add(label);
            friendList.getItems().add(offlineFriends.get(i).toString());
        }

        handler.closeConnection();

    }

    //returns change password menu item
    public MenuItem getChangePassword(){
        return changePassword;
    }




    //returns the add friend menu item
    public MenuItem getAddFriendMenuItem(){
        return addFriendMenu;
    }
    //return the boolean textpartner for event handling(textarea messagetext)
    public boolean getTextPartnerBoolean(){
        return textpartner;
    }

    //return the friend list view
    public ListView getFriendList(){
        return friendList;
    }

    //return the the name of the friend we are currently chatting with
    public String getFriendName(){
        return friendName.getText();
    }

    //return the send message textfield
    public TextArea getMessageText(){
        return messageText;
    }
    //creates scene and returns it
    public Scene getScene(){
        Scene scene = new Scene(layout,500,430);
        return scene;

    }
    //returns the chatbox
    public TextArea getMessageBox(){
        return conversationArea;
    }


    //returns disconnect menu item
    public MenuItem getDisconnect(){
        return disconnect;
    }

    //creates current time string and returns it
    public String getDate(){
        int hour = LocalDateTime.now().getHour();
        int minute = LocalDateTime.now().getMinute();
        int second = LocalDateTime.now().getSecond();
        String date = hour + ":" + minute+ ":"+ second;
        return date;
    }







}
