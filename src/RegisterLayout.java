import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//class for register new account
public class RegisterLayout {
    //variables
    TextField nameText;
    TextField emailText;
    PasswordField passwordText;
    PasswordField confirmpasswordText;
    Button deleteImageButton;
    FileChooser imageUploader;
    ImageView imageView;
    Image image;
    Label failLabel;
    Label imageLabel;
    Stage window;

    //display the scene
    public void display(){
        //init window
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Messenger");

        //image view
        imageView = new ImageView();



        //File chooser
        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        imageUploader = new FileChooser();
        imageUploader.setTitle("Import image");
        imageUploader.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        //////////////////LAYOUT////////////////////////////////////////
        //Labels
        Label registerLabel = new Label();
        registerLabel.setText("Register now!");
        registerLabel.setId("bold-label");

        Label nameLabel = new Label();
        nameLabel.setText("                 Name: ");

        Label emailLabel = new Label();
        emailLabel.setText("               E-mail: ");

        Label passwordLabel = new Label();
        passwordLabel.setText("            Password: ");

        Label confirmpassword = new Label();
        confirmpassword.setText("Confirm password: ");

        imageLabel = new Label();
        imageLabel.setText("");

        failLabel = new Label();
        failLabel.setText("");
        failLabel.setId("fail-label");

        //Textfields
        nameText = new TextField();
        nameText.setMaxWidth(200);

        emailText = new TextField();
        emailText.setMaxWidth(200);

        passwordText = new PasswordField();
        passwordText.setMaxWidth(200);

        confirmpasswordText = new PasswordField();
        confirmpasswordText.setMaxWidth(200);

        //Buttons
        Button registerButton = new Button();
        registerButton.setText("Register!");
        registerButton.setOnAction(e-> {
            Boolean answer;
            //check if everything is ok
            answer = check();

            if(answer){
                byte[]b = null;
                //convert image to bytes
                try {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image,null);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    ImageIO.write(bufferedImage,"png",stream);
                    stream.flush();
                    b = stream.toByteArray();

                }catch (IOException ex){ex.printStackTrace();}


                //init database handler and try register
                dbHandler handler = new dbHandler();
                String message = handler.register(nameText.getText(),emailText.getText(),passwordText.getText(),b);

                if(message.contains("success")){
                    //we could successfully register, popup information window
                    JOptionPane.showMessageDialog(null, "Registration successful! You can login now.","Registration",JOptionPane.INFORMATION_MESSAGE);
                    window.close();


                }
                else{
                    //not successfull which means name or email are already in use
                    failLabel.setText(message);
                }



            }


        });

        Button cancelButton = new Button();
        cancelButton.setText("Cancel");
        cancelButton.setOnAction(e-> window.close());

        Button imageButton = new Button();
        imageButton.setText("Import image");
        imageButton.setOnAction(e-> importImage());

        deleteImageButton = new Button();
        deleteImageButton.setText("delete image");
        deleteImageButton.setVisible(false);
        //delete the selected image
        deleteImageButton.setOnAction(e->{
            image = null;
            imageLabel.setGraphic(null);
            deleteImageButton.setVisible(false);

        });


        ////////////////////BOXES/////////////////////////

        //HBoxes
        //name fields
        HBox box1 = new HBox();
        box1.getChildren().addAll(nameLabel,nameText);
        box1.setAlignment(Pos.CENTER);
        //email fields
        HBox box2 = new HBox();
        box2.getChildren().addAll(emailLabel,emailText);
        box2.setAlignment(Pos.CENTER);
        // password fields
        HBox box3 = new HBox();
        box3.getChildren().addAll(passwordLabel,passwordText);
        box3.setAlignment(Pos.CENTER);
        //con firm password fields
        HBox box6 = new HBox();
        box6.getChildren().addAll(confirmpassword,confirmpasswordText);
        box6.setAlignment(Pos.CENTER);
        //vbox for image buttons
        VBox imagebox = new VBox();
        imagebox.getChildren().addAll(imageButton,deleteImageButton);
        imagebox.setSpacing(10);

        // image upload
        HBox box4 = new HBox();
        box4.getChildren().addAll(imageLabel,imagebox);
        box4.setAlignment(Pos.CENTER);
        box4.setSpacing(10);
        //buttons
        HBox box5 = new HBox();
        box5.getChildren().addAll(registerButton,cancelButton);
        box5.setAlignment(Pos.CENTER);
        box5.setSpacing(20);

        //Main box
        VBox mainbox = new VBox();
        mainbox.getChildren().addAll(registerLabel,box1,box2,box3,box6,box4,box5,failLabel);
        mainbox.setAlignment(Pos.CENTER);
        mainbox.setSpacing(20);

        //Scene
        Scene scene = new Scene(mainbox,400,520);
        scene.getStylesheets().add("style.css");
        window.setScene(scene);
        window.showAndWait();


    }

    //get message from server and register
    public void register(String message){
        if (message.contains("success"))
            failLabel.setText(message);
        else
            failLabel.setText(message);

    }
    //check password and email before registration
    public boolean check(){
        String message = "";
        boolean answer = false;

        //verify
        if(!nameText.getText().isEmpty()&& !emailText.getText().isEmpty() && !passwordText.getText().isEmpty() && !confirmpasswordText.getText().isEmpty()  ){
            //check if password is longer than 7 characters and e-mail is valid
            if(passwordText.getText().length() > 8){
                //check if password and confirm password are the same
                if(passwordText.getText().equals(confirmpasswordText.getText())){
                    //check if the email is valid
                    if(isValidEmail(emailText.getText())){
                        //everything is ok so return true
                        answer = true;
                    }
                    else {
                        //email is not valid, return false
                        message +="E-Mail is not valid! \n";
                        failLabel.setText(message);
                        answer = false;
                    }
                }
                else {
                    //password and confirm password are not equal,return false
                    message += "password and confirm password must be the equal!\n";
                    failLabel.setText(message);
                    answer = false;
                }

            }
            else {
                //password is shorter than 8 characters,return false
                message += "password must be longer than 8 characters! \n";
                failLabel.setText(message);
                answer = false;
            }
        }
        else {
            //some fields are empty so return false
            if(nameText.getText().isEmpty())
                message+="No name inserted! \n";
            if(emailText.getText().isEmpty())
                message+="No email inserted! \n";
            if(passwordText.getText().isEmpty())
                message+="No password inserted!\n";
            if(confirmpasswordText.getText().isEmpty())
                message+="No confirm password inserted!\n";
            answer = false;
            failLabel.setText(message);

        }
        //return answer
        return answer;

    }

    //import an image via file chooser
    public void importImage(){

        File file = imageUploader.showOpenDialog(null);
        //create image
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            //display image on label
            imageLabel.setGraphic(imageView);
            //show delete image button
            deleteImageButton.setVisible(true);

        } catch (IOException ex){
            failLabel.setText("Cannot load image");
        }



    }

    //check if email is valid and return boolean
    public static boolean isValidEmail(String email){
        String EMAIL_REGIX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(EMAIL_REGIX);
        Matcher matcher =  pattern.matcher(email);
        return matcher.matches();
    }



}
