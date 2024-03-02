/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yumxpress.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author PUSHPRAJ
 */
public class DBConnection {
    private static Connection conn;
    static{
//        STATIC BLOCK MEI CONNECTION KA CODE ISLIYE LIKH RAHE HAIN TAAKI CONNECTION OPEN SIRF EK BAAR HO
        try{
            Class.forName("oracle.jdbc.OracleDriver"); //ISS LINE SE HAM KHUDSE DRIVER LOAD KRRHE HAIN
            conn=DriverManager.getConnection("jdbc:oracle:thin:@//Pushpraj-ASUS-TUF:1521/XE","yumxpress", "foodie");
            JOptionPane.showMessageDialog(null,"Connection opened successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(ClassNotFoundException ex){
            JOptionPane.showMessageDialog(null, "Error in loading the driver!", "Driver Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            System.exit(1);
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "Error in opening connection!", "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            System.exit(1);
        }
    }
    public static Connection getConnection(){
        return conn; 
    }
//            KYOKI ISS CLASS MEI KOI BHI NON-STATIC MEMBER NHI H ISLIYE SAARE METHODS STATIC BANAYENGE
    public static void closeConnection(){
        try{
            conn.close();
            JOptionPane.showMessageDialog(null,"Connection closed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "Error in closing the connection!", "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    } 
}
