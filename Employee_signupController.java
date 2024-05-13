/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kemri_visitors_project;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Daniel Macharia
 */
public class Employee_signupController implements Initializable {
    
    @FXML private TextField staff_number;
    @FXML private TextField staff_name;
    @FXML private TextField staff_phone_number;
    @FXML private PasswordField staff_password;
    @FXML private PasswordField staff_confirm_password;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void logInAction( ActionEvent event )
    {
        try
        {
            Stage stage = (Stage)((Scene)((Button)event.getSource()).getScene()).getWindow();
        
            Parent root = FXMLLoader.load( getClass().getResource("Login.fxml"));

            Scene scene = new Scene( root );

            stage.setScene( scene );
            stage.show();
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    
    @FXML
    private void signUpAction( ActionEvent event )
    {
        try
        {
            
            String number = staff_number.getText();
            String name = staff_name.getText();
            String phone = staff_phone_number.getText();
            String password = staff_password.getText();
            String confirm = staff_confirm_password.getText();
            
            if( validInput( number, name, phone, password, confirm) )
            {
                registerSecurityPerson( number, name, phone, UtilityClass.encrypt( password) );
            }
            
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    
    private boolean validInput( String staffNumber, String staffName, String staffPhone, String staffPassword, String confirmPassword)
    {
        if( !UtilityClass.isValidStaffNumber( staffNumber ) )
        {
            UtilityClass.alert("Invalid Staff Number!\n");
            return false;
        }
        
        if( !UtilityClass.isValidUsername(staffName) )
        {
            UtilityClass.alert("Invalid username!\nusername may contain only characters, a space and an apostrophe.");
            return false;
        }
        
        if( !UtilityClass.isValidPhoneNumber(staffPhone) )
        {
            UtilityClass.alert("Invalid phone number!\nPlease enter a valid kenyan phone number.\n i.e 07... or 01... and must contain only ten digits.");
            return false;
        }
        
        if( staffPassword.equals( confirmPassword ) )
        {
            if( !UtilityClass.isValidPassword( staffPassword) )
            {
                UtilityClass.alert("Invalid password!\nA password must be atleast 8 characters long, contain atleast one digit,\n an upper and lower case character and one special character i.e @#$%()_");
                return false;
            }
        }
        else
        {
            UtilityClass.alert( "The password and the confirm password do not match!");
            return false;
        }
        
        
        return true;
    }
    
    private void registerSecurityPerson( String number, String name, String phone, String password )
    {
        try
        {
            String sql = "INSERT INTO SECURITY_STAFF VALUES( ?, ?, ?, ?)";
            
            Connection con = UtilityClass.getDatabaseConnection();
            
            PreparedStatement state = con.prepareStatement(sql);
            state.setString( 1, number);
            state.setString( 2, name);
            state.setString( 3, phone);
            state.setString( 4, password);
            
            int rows = state.executeUpdate();
            
            if( rows == 1 )
            {
                UtilityClass.inform("Registration Successful.");
                clearSignUpDetails();
            }
            else
            {
                UtilityClass.alert("Failed to register!");
            }
            
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    
    private void clearSignUpDetails()
    {
        staff_number.setText("");
        staff_phone_number.setText("");
        staff_name.setText("");
        staff_password.setText("");
        staff_confirm_password.setText("");
    }
    
}
