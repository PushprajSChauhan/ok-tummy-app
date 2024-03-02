/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yumxpress.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import yumxpress.dbutil.DBConnection;
import yumxpress.pojo.StaffPojo;

/**
 *
 * @author PUSHPRAJ
 */
public class StaffDAO {
//    NEW ID GENERATION FOR STAFF MEMBER
    public static String getNewId() throws SQLException{
        Connection conn=DBConnection.getConnection();
        Statement st=conn.createStatement();
        ResultSet rs=st.executeQuery("select max(staff_id) from staff");
        rs.next();
        String id=rs.getString(1);
        String staffId="";
        if(id!=null){
            id=id.substring(4);
            staffId="STF-"+(Integer.parseInt(id)+1);
        }
        else{
            staffId="STF-101";
        }
        return staffId;
    }
    
//    ADDING STAFF TO COMPANY
    public static String addStaff(StaffPojo staff) throws SQLException{
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("insert into staff values(?,?,?,?,?)");
        staff.setStaffId(getNewId());
        ps.setString(1, staff.getStaffId());
        ps.setString(2, staff.getCompanyId());
        ps.setString(3,staff.getEmailId());
        ps.setString(4, staff.getPassword());
        ps.setString(5, staff.getStaffName());
       
        return (ps.executeUpdate()==1)?staff.getStaffId():null;
    }
    
//    GETTING ALL STAFF MEMBERS HIRED BY A COMPANY
    public static List<String> getAllStaffIdByCompanyId(String companyId) throws SQLException{
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("select staff_id from staff where company_id=?");
        ps.setString(1, companyId);
//        LIST KI JAGAH SET KA USE BHI KRSKTE HAIN BCOS STAFF ID KI VALUES UNIQUE HI HAIN
        List<String> staffList=new ArrayList<>();
        ResultSet rs=ps.executeQuery();
        while(rs.next()){
            staffList.add(rs.getString(1));
        }
        return staffList;
    }
    
//    GETTING DETAILS OF ALL STAFF HIRED BY A COMPANY
    public static StaffPojo getStaffDetailsById(String staffId) throws SQLException{
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("select * from staff where staff_id=? order by staff_id");
        ps.setString(1, staffId);
        StaffPojo staff=new StaffPojo();
        ResultSet rs=ps.executeQuery();
        rs.next();
        staff.setStaffName(rs.getString(5));
        staff.setEmailId(rs.getString(3));
        return staff;
    }
    
//    GETTING A STAFF FOR DELIVERING THE ORDER
    public static String getRandomStaffIdFromCompany(String companyId) throws SQLException{
        List<String> staffList=StaffDAO.getAllStaffIdByCompanyId(companyId);
        Random rndm=new Random();
        int index=rndm.nextInt(staffList.size());
        return staffList.get(index);
    }
    
//    VALIDATE KRENGE STAFF MEMBER KO DURING LOGIN
    public static StaffPojo validate(String emailId,String password)throws SQLException{
       Connection conn=DBConnection.getConnection();
       PreparedStatement ps=conn.prepareStatement("select staff_id,staff_name from staff where email_id=? and password=?");
       ps.setString(1, emailId);
       ps.setString(2, password);
       ResultSet rs=ps.executeQuery();
       StaffPojo staff=null;
       if(rs.next()){
           staff=new StaffPojo();
           staff.setStaffId(rs.getString(1));
           staff.setStaffName(rs.getString(2));
           staff.setEmailId(emailId);
       }
       //        YAHA SE POORA POJO RETURN KRNE KI JAGAH SIRF APNE REQUIRED DATA MEMBERS KA EK VALUE OBJECT BANAKE RETURN KRSKTE HAIN JO POJO KA SUB-OBJECT HOTA H
       return staff;       
   }
    
    public static String getStaffIdByName(String staffName) throws SQLException{
       Connection conn=DBConnection.getConnection();
       PreparedStatement ps=conn.prepareStatement("select staff_id from staff where staff_name=?");
       ps.setString(1, staffName);
       String staffId="";
       ResultSet rs=ps.executeQuery();
       if(rs.next()){
           staffId=rs.getString("staff_id");
       }
       return staffId;
    }
}
