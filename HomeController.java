/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kemri_visitors_project;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Daniel Macharia
 */
public class HomeController implements Initializable {
    
    //check in
    @FXML private TextField visit_reason;

    @FXML private TextField number_plate_input;

    @FXML private Label number_plate_label;

    @FXML private TextField visitor_id;

    @FXML private RadioButton visit_by_vehicle;

    @FXML private TextField visitor_origin;

    @FXML private RadioButton visit_on_foot;

    @FXML private ComboBox<String> visitor_destination;
    
    private ToggleGroup foot_or_vehicle;
    
    //check out
    @FXML private TextField visitor_number_or_ID;
    
    //register
    @FXML private TextField visitor_ID_number;
    @FXML private TextField visitor_name;
    @FXML private TextField visitor_phone_number;
    
    //report
    @FXML private DatePicker date_to_report;
    @FXML private ListView report_list;
    
    
    private final ArrayList<GridPane> reportList = new ArrayList<>();
    private final Destination destinationList = new Destination();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        foot_or_vehicle = new ToggleGroup();
        visit_on_foot.setToggleGroup(foot_or_vehicle);
        visit_by_vehicle.setToggleGroup(foot_or_vehicle);
        
        initDestination();
        
        GridPane titles = getGridPane("Visitor ID",
                        "Visitor name",
                        "Visitor phone",
                        "Visitor`s origin",
                        "Visitor`s destination",
                        "Reason for visit",
                        "Time in",
                        "Time out" );
        
        reportList.add(titles);
        updateList();
    }
    
    private void initDestination()
    {
        try
        {
            //init the destination combo box
            String sql = "SELECT * FROM DESTINATION";
            Connection con = UtilityClass.getDatabaseConnection();
            
            PreparedStatement state = con.prepareStatement(sql);
            
            ResultSet result = state.executeQuery();
            
            while( result.next() )
            {
                destinationList.add( result.getString(1), result.getString(2) );
            }
            
            ObservableList<String> items = FXCollections.observableArrayList(destinationList.getNames());
            
            visitor_destination.setItems(items);
            
        }catch( Exception e )
        {
            System.out.println("Error: " + e);
        }
    }
    
    //check in
    @FXML 
    private void registerVisitorAction(ActionEvent event)
    {
        try
        {
            String id_number = visitor_ID_number.getText();
            String name = visitor_name.getText();
            String phone = visitor_phone_number.getText();
            
            if( validVisitorRegistrationDetails( id_number, name, phone ) )
            {
                registerNewVisitor( id_number, name, phone );
            }
            
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    
    @FXML
    private void checkOutAction( ActionEvent event )
    {
        try
        {
            String number = visitor_number_or_ID.getText();
            
            //UtilityClass.alert("Number: " + number);
            
            if( isValidCheckOutId( number ) )
            {
                checkOutVisitor( number );
            }
            
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    private boolean isValidCheckOutId( String idNumber )
    {
        if( !UtilityClass.isValidCheckInId( idNumber ) )
        {
            UtilityClass.alert("Please enter a valid id or visit number.");
            return false;
        }
        
        return true;
    }
    
    private void checkOutVisitor( String idNumber )
    {
        try
        {
            Date date = new Date();
            Scanner s = new Scanner( date.toString() );
        
            String y, m, d, time_out;

            s.next();
            m = getMonth( s.next() ) + "";
            d = s.next();
            time_out = s.next();
            s.next();
            y = s.next();

            String dateString = y + "-" + m + "-" + d;
            
            
            String sql = "UPDATE VISIT SET VISITOR_TIME_OUT = ? "
                    + "WHERE (VISIT_DATE = ? AND VISIT_NUMBER = ?) OR "
                    + "(VISIT_DATE = ? AND VISITOR_ID_NUMBER = ? )";
            
            Connection con = UtilityClass.getDatabaseConnection();
            PreparedStatement state = con.prepareStatement(sql);
            
            state.setString( 1, time_out);
            state.setString(2, dateString);
            state.setString(3, idNumber);
            state.setString(4, dateString);
            state.setString(5, idNumber);
            
            int rows = state.executeUpdate();
            
            if( rows == 1 )
            {
                clearCheckOutDetails();
                UtilityClass.inform("Successfully checked out!");
            }
            else
            {
                UtilityClass.alert("Failed to check out!\nConfirm the entered ID or visit number.");
            }
            
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
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
    
    @FXML
    private void generateVisitReportAction(ActionEvent event)
    {
        try
        {
            String date = date_to_report.getValue().toString();
            
            getReport();
            
            //UtilityClass.alert("Date: " + date );
            
        }catch( NullPointerException e )
        {
            UtilityClass.alert("No date selected!");
        }
        catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    
    
    private void getReport()
    {
        try
        {
            String sql = "SELECT VISIT.VISITOR_ID_NUMBER, VISITOR_NAME, VISITOR_PHONE_NUMBER,"
                    + "VISITOR_ORIGIN, DESTINATION_NAME, VISIT_REASON, VISITOR_TIME_IN, VISITOR_TIME_OUT "
                    + "FROM VISIT JOIN VISITOR ON VISIT.VISITOR_ID_NUMBER=VISITOR.VISITOR_ID_NUMBER "
                    + "JOIN DESTINATION ON VISIT.VISITOR_DESTINATION_ID=DESTINATION.DESTINATION_ID";
            
            Connection con = UtilityClass.getDatabaseConnection();
            
            PreparedStatement state = con.prepareStatement( sql );
            
            ResultSet result = state.executeQuery();
            
            while( result.next() )
            {
                reportList.add( getGridPane( result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5),
                        result.getString(6),
                        result.getString(7),
                        result.getString(8)) );
            }
            
            updateList();
        }catch( Exception e )
        {
            UtilityClass.alert("Error: "+ e);
        }
    }
    
    private void updateList()
    {
        ObservableList<GridPane> visitReport = FXCollections.observableArrayList( reportList );
        report_list.setItems(visitReport);
    }
    
    private GridPane getGridPane(String v_id, String v_name, String v_phone, String v_origin, String v_destination, String v_reason, String v_time_in, String v_time_out)
    {
        GridPane pane = new GridPane();
        
        Label vIDLabel = new Label(v_id);
        vIDLabel.setMinWidth(120.0);
        vIDLabel.setMaxWidth(120.0);
        
        Label vNameLabel = new Label(v_name);
        vNameLabel.setMinWidth(180.0);
        vNameLabel.setMaxWidth(180.0);
        
        Label vPhoneLabel = new Label(v_phone);
        vPhoneLabel.setMinWidth(120.0);
        vPhoneLabel.setMaxWidth(120.0);
        
        Label vOriginLabel = new Label(v_origin);
        vOriginLabel.setMinWidth(120.0);
        vOriginLabel.setMaxWidth(120.0);
        
        Label vDestinationLabel = new Label(v_destination);
        vDestinationLabel.setMinWidth(180.0);
        vDestinationLabel.setMaxWidth(180.0);
        
        Label vReasonLabel = new Label(v_reason);
        vReasonLabel.setMinWidth(200.0);
        vReasonLabel.setMaxWidth(200.0);
        
        Label vTimeInLabel = new Label(v_time_in);
        vTimeInLabel.setMinWidth(180.0);
        vTimeInLabel.setMaxWidth(180.0);
        
        Label vTimeOutLabel = new Label(v_time_out);
        vTimeOutLabel.setMinWidth(100.0);
        vTimeOutLabel.setMaxWidth(100.0);
        
        pane.add( vIDLabel, 0,0);
        pane.add( vNameLabel, 1, 0);
        pane.add( vPhoneLabel, 2, 0);
        pane.add( vOriginLabel, 3, 0);
        pane.add( vDestinationLabel, 4, 0);
        pane.add( vReasonLabel, 5, 0);
        pane.add( vTimeInLabel, 6, 0);
        pane.add( vTimeOutLabel, 7, 0);
        
        return pane;
    }
    
    @FXML
    private void onFootAction( ActionEvent event )
    {
        showNumberPlateField(false);
    }
    
    @FXML
    private void byVehicleAction( ActionEvent event )
    {
        showNumberPlateField(true);
        //number_plate_input.setText("");
       // number_plate_input.clear();
    }
    
    private void showNumberPlateField(boolean byVehicle)
    {
        try
        {
            
            number_plate_label.setVisible(byVehicle);
            number_plate_input.setVisible(byVehicle);
            
        }catch( Exception e )
        {
            System.out.println("Error: " + e);
        }
            
    }
    
    @FXML
    private void checkInAction(ActionEvent event )
    {
        try{
            String id = visitor_id.getText();
            String origin = visitor_origin.getText();
            String destination = visitor_destination.getValue();
            String reason = visit_reason.getText();
            
            
            String numberPlate = null;
            
            if( visit_by_vehicle.isSelected() )
            {
                numberPlate = number_plate_input.getText();
            }
            else if( visit_on_foot.isSelected() )
            {
                numberPlate = "NULL";
            }
            
            if( validCheckInDetails( id, origin, reason, numberPlate ) )
            {
                checkInVisitor( id, origin, destination, reason, numberPlate );
            }
            
        }catch( Exception e )
        {
            System.out.println("Error: " + e);
        }
    }
    
    private boolean validVisitorRegistrationDetails( String idNumber, String name, String phone )
    {
        if( !UtilityClass.isValidIdNumber( idNumber ) )
        {
            UtilityClass.alert("Invalid ID number!\nAn id number may contain only 8 digits.");
            return false;
        }
        
        if( !UtilityClass.isValidUsername( name ) )
        {
            UtilityClass.alert("Invalid name.\nA name may contain characters, a space or an apostrophe.");
            return false;
        }
        
        if( !UtilityClass.isValidPhoneNumber( phone ) )
        {
            UtilityClass.alert("Invalid phone number.\nEnter a valid Kenyan phone number\n e.g 07... or 01... and must be 10 digits long.");
            return false;
        }
        
        
        return true;
    }
    
    private void registerNewVisitor( String idNumber, String name, String phoneNumber )
    {
        try
        {
            String sql = "INSERT INTO VISITOR VALUES( ?, ?, ?)";
            
            Connection con = UtilityClass.getDatabaseConnection();
            
            PreparedStatement state = con.prepareStatement(sql);
            
            state.setString(1, idNumber);
            state.setString(2, name);
            state.setString(3, phoneNumber);
            
            int rows = state.executeUpdate();
            
            if( rows == 1 )
            {
                UtilityClass.inform("Visitor registered successfully!");
                clearRegistrationDetails();
            }
            else
            {
                UtilityClass.alert("Fialed to register visitor!");
            }
        }catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
    
    private void clearCheckInDetails()
    {
        visitor_id.setText("");
        visitor_origin.setText("");
        visitor_destination.setValue("");
        visit_reason.setText("");
        
        visit_on_foot.setSelected(false);
        visit_by_vehicle.setSelected(false);
        number_plate_input.setText("");
        showNumberPlateField(false);
        
    }
    
    private void clearCheckOutDetails()
    {
        visitor_number_or_ID.setText("");
    }
    
    private void clearRegistrationDetails()
    {
        visitor_ID_number.setText("");
        visitor_name.setText("");
        visitor_phone_number.setText("");
    }
    
    private boolean validCheckInDetails( String id, String origin, String reason, String numberPlate)
    {
        if( !UtilityClass.isValidIdNumber( id ) )
        {
            UtilityClass.alert("Invalid ID number.\nId number contains 8 digits.");
            return false;
        }
        
        if( !UtilityClass.isValidOrigin( origin ) )
        {
            UtilityClass.alert("Origin may contain only characters and digits");
            return false;
        }
        
        if( !UtilityClass.isValidUsername(reason) )
        {
            UtilityClass.alert("Reason may contain only characters, a space and an apostrophe.");
            return false;
        }
        
        if( numberPlate == null )
        {
            UtilityClass.alert("Please select means!");
            return false;
        }
        else if( !numberPlate.equals("NULL") )
        {
            if( !UtilityClass.isValidNumberPlate( numberPlate ) )
            {
                UtilityClass.alert("Invalid number plate!\nNumber plate contains 3 letters followed by a space then \n 3 digits followed by a character e.g XYZ 222H");
                return false;
            }
        }
        
        return true;
    }
    
    private void checkInVisitor( String id, String origin, String destination, String reason, String numberPlate )
    {
        try
        {
            Date date = new Date();
            Scanner s = new Scanner( date.toString() );
        
            String y, m, d;

            s.next();
            m = getMonth( s.next() ) + "";
            d = s.next();
            s.next();
            s.next();
            y = s.next();

            String dateString = y + "-" + m + "-" + d;
            
            String time_out = "";
            
            String sql = "INSERT INTO VISIT(VISIT_DATE,"
                    + "VISIT_NUMBER,"
                    + "VISITOR_ID_NUMBER,"
                    + "VISITOR_ORIGIN,"
                    + "VISITOR_DESTINATION_ID,"
                    + "VISIT_REASON,"
                    + "VISITOR_TIME_OUT,"
                    + "VEHICLE_NUMBER_PLATE) "
                    + "VALUES( ?, ?, ?, ?, ?, ?, ?, ?)";
            
            Connection con = UtilityClass.getDatabaseConnection();
            
            PreparedStatement state = con.prepareStatement( sql );
            
            state.setString(1, dateString);
            state.setInt(2, UtilityClass.visitCount + 1);
            state.setString(3, id);
            state.setString(4, origin);
            state.setString(5, destinationList.getId( destination ) );
            state.setString(6, reason);
            //state.setString(7, time_in);
            state.setString(7, time_out);
            state.setString(8, numberPlate);
            
            int rows = state.executeUpdate();
            
            if( rows == 1 )
            {
                DateNumber.incrementDBCount();
                clearCheckInDetails();
                UtilityClass.inform("Successfully checked in\n" + "Your visit number is\n\n" + (UtilityClass.visitCount) );
                
            }
            else
            {
                UtilityClass.alert("Failed to check in!");
            }
            
        }catch(MySQLIntegrityConstraintViolationException e )
        {
            UtilityClass.alert("Visitor not Registered!\nPlease Register first.");
        }
        catch( Exception e )
        {
            UtilityClass.alert("Error: " + e);
        }
    }
}


class Destination
{
    private ArrayList<String> idList;
    private ArrayList<String> nameList;
    
    public Destination( )
    {
        idList = new ArrayList<String>();
        nameList = new ArrayList<String>();
    }
    
    public void add( String id, String name )
    {
        idList.add( new String(id) );
        nameList.add( new String( name ) );
    }
    
    public String getId( int index )
    {
        return idList.get( index );
    }
    
    public String getId( String name )
    {
        for( int i = 0; i < nameList.size(); i++ )
        {
            if( name.matches( nameList.get( i) ) )
                return idList.get(i);
        }
        
        return null;
    }
    
    public String getName( int index )
    {
        return nameList.get( index );
    }
    
    public ArrayList<String> getNames()
    {
        ArrayList<String> items = new ArrayList<>();
        
        for( String name : nameList )
            items.add( new String( name ) );
        
        return items;
    }
}