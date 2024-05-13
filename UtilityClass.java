/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kemri_visitors_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Alert;

/**
 *
 * @author Daniel Macharia
 */
public class UtilityClass {
    
    public static int visitCount = 0;
    
    
    public static void alert(String message )
    {
        //System.out.println(message);
        Alert alert;
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Kindly Note.");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void inform(String message )
    {
        //System.out.println(message);
        Alert alert;
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information.");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static boolean isValidUsername( String username )
    {
        String usernameRegex = "^[a-zA-Z`\\s]{1,100}$";
        
        return username.matches( usernameRegex );
    }
    
    public static boolean isValidPassword( String password )
    {
        String lowerAlphaRegex = "^[\\w\\W]{0,20}[a-z]{1,20}[\\w\\W]{0,20}$";
        String upperAlphaRegex = "^[\\w\\W]{0,20}[A-Z]{1,20}[\\w\\W]{0,20}$";
        String integerRegex = "^[\\w\\W]{0,20}[0-9]{1,20}[\\w\\W]{0,20}$";
        String specialCharacterRegex = "^[\\w\\W]{0,20}[@#$%()_]{1,20}[\\w\\W]{0,20}$";
        
        return (password.length() >= 8) && password.matches( lowerAlphaRegex ) && password.matches( upperAlphaRegex) 
                && password.matches( integerRegex ) && password.matches( specialCharacterRegex );
    }
    
    public static boolean isValidPhoneNumber( String phoneNumber )
    {
        String phoneNumberRegex = "^0[17]{1}[0-9]{8}$";
        
        return phoneNumber.matches( phoneNumberRegex );
    }
    
    public static boolean isValidIdNumber( String idNumber )
    {
        String idRegex = "^[0-9]{8}$";
        
        return idNumber.matches( idRegex );
    }
    
    public static boolean isValidCheckInId( String idNumber )
    {
        String idRegex = "^[0-9]{1,8}$";
        
        return idNumber.matches( idRegex );
    }
    
    public static boolean isValidNumberPlate( String numberPlate )
    {
        //String charRegex = "^[0-9]{0,5}[a-zA-Z]{1,5}[0-9]{0,5}$";
        //String digitRegex = "^[a-zA-Z]{0,5}[0-9]{1,5}[a-zA-Z]{0,5}$";
        String regex = "^[a-zA-Z]{3}\\s[0-9]{3}[a-zA-Z]{1}$";
        
        //return numberPlate.matches( charRegex ) && numberPlate.matches( digitRegex );
        return numberPlate.matches( regex );
    }
    
    public static boolean isValidStaffNumber( String staffNumber )
    {
        String staffNumberRegex = "^[\\w\\W]{1,10}$";
        
        return staffNumber.matches(staffNumberRegex);
    }
    
    public static boolean isValidOrigin( String origin )
    {
        String originRegex = "^[a-zA-Z0-9]{1,100}$";
        
        return origin.matches( originRegex );
    }
    
    public static String encrypt( String text )
    {
        String cipher = new String( text );
        
        return cipher;
    }
    
    
    public static Connection getDatabaseConnection()
    {
        String username = "root";
        String password = "steve";
        String url = "jdbc:mysql://localhost:3306/KEMRI_VISITORS_PROJECT";
        
        Connection con = null;
        
        try
        {
            con = DriverManager.getConnection( url, username, password);
        }catch( SQLException e )
        {
            UtilityClass.alert("Database Error: " + e);
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
        
        return con;
    }
    
}
