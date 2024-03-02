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
import yumxpress.dbutil.DBConnection;
import yumxpress.pojo.CompanyPojo;
import yumxpress.pojo.CustomerPojo;

/**
 *
 * @author PUSHPRAJ
 */
public class CustomerDAO {
//    GENERATING NEW CUSTOMER ID
    public static String getNewId() throws SQLException{
        Connection conn=DBConnection.getConnection();
        Statement st=conn.createStatement();
        ResultSet rs=st.executeQuery("select max(customer_id) from customers");
        rs.next();
        String id=rs.getString(1);
        String customerId="";
        if(id!=null){
            id=id.substring(4);
            customerId="CST-"+(Integer.parseInt(id)+1);
        }
        else{
            customerId="CST-101";
        }
        return customerId;
    }
    
//    ADDING NEW CUSTOMER
    public static boolean addCustomer(CustomerPojo customer) throws SQLException{
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("insert into customers values(?,?,?,?,?,?)");
        customer.setCustomerId(getNewId());
        ps.setString(1, customer.getCustomerId());
        ps.setString(2, customer.getCustomerName());
        ps.setString(3, customer.getEmailId());
        ps.setString(4, customer.getPassword());
        ps.setString(5, customer.getMobileNo());
        ps.setString(6, customer.getAddress());
       
        return ps.executeUpdate()==1;
    }
    
//    USER VALIDATION
    public static CustomerPojo validate(String emailId,String password)throws SQLException{
       Connection conn=DBConnection.getConnection();
       PreparedStatement ps=conn.prepareStatement("select * from customers where email_id=? and password=?");
       ps.setString(1, emailId);
       ps.setString(2, password);
       ResultSet rs=ps.executeQuery();
       CustomerPojo customer=null;
       if(rs.next()){
           customer=new CustomerPojo();
           customer.setEmailId(rs.getString("email_id"));
//           customer.setPassword(rs.getString(2));
//           customer.setAddress(rs.getString("customer_address"));
           customer.setCustomerName(rs.getString("customer_name"));
           customer.setCustomerId(rs.getString("customer_id"));
           customer.setMobileNo("mobile_no");
       }
       //        YAHA SE POORA POJO RETURN KRNE KI JAGAH SIRF APNE REQUIRED DATA MEMBERS KA EK VALUE OBJECT BANAKE RETURN KRSKTE HAIN JO POJO KA SUB-OBJECT HOTA H
       return customer ;       
   }
    
//    GETTING CUSTOMER'S DETAILS BY UNIQUE ID
    public static CustomerPojo getCustomerDetailsById(String customerId) throws SQLException{
       Connection conn=DBConnection.getConnection();
       PreparedStatement ps=conn.prepareStatement("select * from customers where customer_id=?");
       ps.setString(1, customerId);
       ResultSet rs=ps.executeQuery();
       CustomerPojo customer=null;
       if(rs.next()){
           customer=new CustomerPojo();
           customer.setEmailId(rs.getString("email_id"));
           customer.setCustomerId(rs.getString(1));
           customer.setCustomerName(rs.getString(2));
           customer.setMobileNo(rs.getString("mobile_no"));
           customer.setPassword(rs.getString("password"));
           customer.setAddress(rs.getString("address"));
       }
       return customer;
    }
    
//    UPDATING DETAILS
    public static boolean updateCustomer(CustomerPojo customer) throws SQLException{
       Connection conn=DBConnection.getConnection();
       PreparedStatement ps=conn.prepareStatement("update customers set customer_name=?, password=?, mobile_no=?, address=? where customer_id=?");
       ps.setString(5, customer.getCustomerId());
       ps.setString(1, customer.getCustomerName());
       ps.setString(2, customer.getPassword());
       ps.setString(3, customer.getMobileNo());
       ps.setString(4, customer.getAddress());
       return ps.executeUpdate()==1;
    }
    
//    LOAD ALL PRODUCTS ORDERED
}
