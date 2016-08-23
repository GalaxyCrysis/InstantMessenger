import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

//opens new dialog to add a new friend
public class FriendHandler {
    String name = "";
    String friend;
    TextField nameField;
    PasswordField oldPasswordField;
    PasswordField newPasswordField;
    Label failLabel;
    Stage dialogStage;
    Button addButton;
    Button cancelButton;
    Label message;
    boolean answer;
    public String getFriendName(){

        dialogStage = new Stage();
        Label information = new Label();
        information.setText("Insert your friends name: ");
        information.setAlignment(Pos.CENTER);

        failLabel = new Label();
        failLabel.setText("");
        failLabel.setTextFill(Color.web("#FF0000"));

        nameField = new TextField();
        nameField.setMaxWidth(200);
        nameField.setAlignment(Pos.CENTER);

        Button addbutton = new Button();
        addbutton.setText("Add Friend");
        addbutton.setOnAction(e->{
            searchFriend();
        });

        Button canclebutton = new Button();
        canclebutton.setText("Cancel");
        canclebutton.setOnAction(e->dialogStage.close());

        HBox buttonbox = new HBox();
        buttonbox.getChildren().addAll(addbutton,canclebutton);
        buttonbox.setAlignment(Pos.CENTER);
        buttonbox.setSpacing(10);

        HBox infoBox = new HBox();
        infoBox.getChildren().addAll(information,nameField);
        infoBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(infoBox,buttonbox,failLabel);
        layout.setSpacing(30);

        Scene scene = new Scene(layout,300,300);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
        return name;



    }
    public boolean addFriend(String user){
        //init dialog
        answer = false;
        dialogStage = new Stage();
        message = new Label();
        message.setText(user + " wants to add you as a friend");
        message.setAlignment(Pos.CENTER);

        addButton = new Button();
        addButton.setText("Add friend");
        addButton.setAlignment(Pos.CENTER);
        //add friend
        addButton.setOnAction(e-> setBoolean());

        cancelButton = new Button();
        cancelButton.setAlignment(Pos.CENTER);
        cancelButton.setText("No thanks");
        cancelButton.setOnAction(e->{
            //we dont want to add the friend to our friendlist, so just close the window
            dialogStage.close();
        });

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(addButton,cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        //main layout
        VBox layout = new VBox();
        layout.getChildren().addAll(message,buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(30);

        //init scene and start the stage
        Scene scene = new Scene(layout,300,300);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();


        //returns the boolean
        return answer;


    }
    //opens a dialog for changing password. Returns a boolean value
    public boolean changePassword(String account_name){
        answer = false;
        //init gui
        dialogStage = new Stage();

        //labels
        Label oldPassword = new Label();
        oldPassword.setText("Old Password:");
        Label newPassword = new Label();
        newPassword.setText("New Password");
        failLabel = new Label();
        failLabel.setText("");
        failLabel.setId("fail-label");

        //Buttons
        Button changeButton = new Button();
        changeButton.setText("Change password");
        changeButton.setOnAction(e->{
            //verify that the new password is correct(aka not null and longer than 8 characters)
            if (newPasswordField.getText().equals(""))
                failLabel.setText("Insert a new password");
            else if(newPasswordField.getText().length() < 8)
                failLabel.setText("Password must be longer than 8 characters!");
            else
                setNewPassword(account_name);


        });

        Button cancleButton = new Button();
        cancleButton.setText("Cancle");
        cancleButton.setOnAction(e->{
            answer = false;
            dialogStage.close();
        });


        //Textfields
        oldPasswordField = new PasswordField();
        oldPasswordField.setMaxWidth(200);

        newPasswordField = new PasswordField();
        newPasswordField.setMaxWidth(200);

        //layout
        HBox box1 = new HBox();
        box1.getChildren().addAll(oldPassword,oldPasswordField);
        box1.setAlignment(Pos.CENTER);

        HBox box2 = new HBox();
        box2.getChildren().addAll(newPassword,newPasswordField);
        box2.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(changeButton,cancleButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);


        VBox layout = new VBox();
        layout.getChildren().addAll(box1,box2,buttonBox,failLabel);
        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout,200,200);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();


        return answer;
    }

    //opens dialog for deleting a friend. the user inserts the name of the friend to be deleted
    //into the textfield. Finally returns the name
    public String deleteFriend(String account_name){
        friend = "";
        dialogStage = new Stage();
        //label
        message = new Label();
        message.setText("Insert the name of the friend you wanna delete:");
        message.setAlignment(Pos.CENTER);

        //textfield
        nameField = new TextField();
        nameField.setMaxWidth(200);
        nameField.setAlignment(Pos.CENTER);

        //buttons
        addButton = new Button();
        addButton.setText("Delete friend");
        addButton.setAlignment(Pos.CENTER);
        //add friend
        addButton.setOnAction(e-> setFriendToBeDeleted(account_name));

        cancelButton = new Button();
        cancelButton.setAlignment(Pos.CENTER);
        cancelButton.setText("Cancel");
        cancelButton.setOnAction(e->{
            //we dont want to delete the friend, so just close the window
            friend ="";
            dialogStage.close();
        });
        //fail label
        failLabel = new Label();
        failLabel.setText("");
        failLabel.setTextFill(Color.web("#FF0000"));

        //init layout
        HBox fieldBox = new HBox();
        fieldBox.getChildren().addAll(message,nameField);
        fieldBox.setAlignment(Pos.CENTER);
        fieldBox.setSpacing(10);

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(addButton,cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        //main layout
        VBox layout = new VBox();
        layout.getChildren().addAll(fieldBox,buttonBox,failLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(30);

        //init scene and start the stage
        Scene scene = new Scene(layout,300,300);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();



        return friend;
    }


    public void setFriendToBeDeleted(String account_name){
        boolean answer;
        //update friend string
        friend = nameField.getText();
        //look into the database if the friend exists
        dbHandler handler = new dbHandler();
        answer = handler.friendExists(account_name,friend);
        if (answer){
            //user exists so close the window
            dialogStage.close();
        }
        else {
            //friend doesnt exist, so inform the user via fail label
            failLabel.setText("Friend doesn't exist");
        }
    }

    //inits Database handler, compares the database password with inserted old password. If true changes to new
    //password
    public void setNewPassword(String account_name){
        dbHandler handler = new dbHandler();
        String message = handler.login(account_name,oldPasswordField.getText());
        //this functions compares the password with the saved password in the database
        if (message.equals("success")){
            //success, now change to new password and close the dialog
            handler.changePassword(account_name,newPasswordField.getText());
            answer = true;
            dialogStage.close();

        }
        else
        {
            failLabel.setText("Old password is wrong!");
        }

    }

    public void setBoolean(){
        //we add a new friend so set the boolean to true
        answer = true;
        dialogStage.close();

    }
    public void searchFriend(){
        //init dbHandler and look if the user exists in the database
        dbHandler handler = new dbHandler();
        boolean answer = handler.userExists(nameField.getText());
        if (answer){
            name = nameField.getText();
            dialogStage.close();

        }
        else {
            failLabel.setText("User doesn't exist");
        }
        handler.closeConnection();
    }

}
