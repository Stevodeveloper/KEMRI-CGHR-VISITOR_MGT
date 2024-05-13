/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kemri_visitors_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.util.Date;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Daniel Macharia
 */
public class KEMRI_Visitors_project extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        DateNumber n = new DateNumber();
        n.disp();
        
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("KEMRI VISITOR MANAGEMENT");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}


class DateNumber
{
    private Date d;
    private int date, year;
    private int month;
    
    public DateNumber()
    {
        d = new Date();
        parse();
    }
    
    private int getMonth( String month )
    {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        for( int i = 0; i < months.length; i++ )
        {
            if( month.matches( months[i] ) )
                return i + 1;
        }
        
        return 0;
    }
    
    private void parse()
    {
        Scanner s = new Scanner( this.d.toString() );
        
        String y, m, d;
        
        s.next();
        m = s.next();
        d = s.next();
        s.next();
        s.next();
        y = s.next();
        
        String date = y + " " + m + " " + d;
        year = Integer.parseInt( y );
        month = getMonth( m );
        this.date = Integer.parseInt( d );
        
        if( greaterThan( getStoredDate() ) )
        {
            //update date and init UtilityClass.visitCount to 0
            updateDBDate();
        }
        
    }
    
    public static void incrementDBCount()
    {
        try
        {
            UtilityClass.visitCount++;
            
            String sql = "UPDATE DATE_NUMBER SET CURRENT_COUNT = ? ";
            
            Connection con = UtilityClass.getDatabaseConnection();
            
            PreparedStatement state = con.prepareStatement(sql);
            
            state.setInt(1, UtilityClass.visitCount );
            
            int rows = state.executeUpdate();
            
            if( rows != 1 )
            {
                UtilityClass.alert("Could not update number!");
            }
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    
    private boolean greaterThan( String storedDate )
    {
        String y, m, d;
        
        if( storedDate != null )
        {
            Scanner s = new Scanner( storedDate );
        
            y = s.next();
            m = s.next();
            d = s.next();
            
            int yr, mn, dt;
            yr = Integer.parseInt(y);
            mn = Integer.parseInt( m );
            dt = Integer.parseInt( d );
            
            if( yr < year )
            {
                return true;
            }
            else//check month
            {
                if( mn < month )
                {
                    return true;
                }
                else//check date
                {
                    if( dt < date )
                    {
                        return true;
                    }
                }
            }
        }
        
        
        
        return false;
    }
    
    private String getStoredDate()
    {
        try
        {
            String sql = "SELECT * FROM DATE_NUMBER";
            
            Connection con = UtilityClass.getDatabaseConnection();
            PreparedStatement state = con.prepareStatement( sql );
            
            ResultSet result = state.executeQuery();
            
            if( result.first() )
            {
                String num, date;
                
                date = result.getString(1);
                UtilityClass.visitCount = result.getInt(2);
                
                return date;
            }
            
            
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
        return null;
    }
    
    private void updateDBDate()
    {
        try
        {
            String sql = "UPDATE DATE_NUMBER SET DATE_TODAY = ?,  CURRENT_COUNT = ?";
            
            Connection con = UtilityClass.getDatabaseConnection();
            
            PreparedStatement state = con.prepareStatement(sql);
            
            state.setString( 1, year + " " + month + " " + date );
            state.setInt( 2, 0);
            
            int rows = state.executeUpdate();
            
            if( rows != 1 )
            {
                UtilityClass.alert("Invalid Date!");
            }
            
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    
    public void disp()
    {
        System.out.println(d.toString());
    }
}
