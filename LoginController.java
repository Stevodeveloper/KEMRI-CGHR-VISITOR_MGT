/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kemri_visitors_project;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Daniel Macharia
 */
public class LoginController implements Initializable {
    @FXML private AnchorPane parent_pane;
    @FXML private Pane content_pane;
    
    @FXML
    private void logInAction(ActionEvent event) {
        try
        {
            Stage stage = (Stage)((Scene)((Button)event.getSource()).getScene()).getWindow();
        
            Parent root = FXMLLoader.load( getClass().getResource("home.fxml"));

            Scene scene = new Scene( root );

            stage.setScene( scene );
            stage.show();
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    
    @FXML
    private void signUpAction(ActionEvent event) {
       try
        {
            Stage stage = (Stage)((Scene)((Button)event.getSource()).getScene()).getWindow();
        
            Parent root = FXMLLoader.load( getClass().getResource("employee_signup.fxml"));

            Scene scene = new Scene( root );

            stage.setScene( scene );
            stage.show();
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try
        {
            //File imgFile = new File("assets/kemri_with_motto.jpg");
            //Image img = new Image("assets/kemri_with_motto.jpg");
            //login_logo_image.setImage(img);
            //ImageView img = new ImageView("assets/KEMRI.png");
        }catch(Exception e)
        {
            UtilityClass.alert("Error: " + e.getMessage());
        } 
    }    
    
}
