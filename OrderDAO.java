/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yumxpress.dao;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import yumxpress.dbutil.DBConnection;
import yumxpress.pojo.CartPojo;
import yumxpress.pojo.OrderPojo;
import yumxpress.pojo.PlaceOrderPojo;
import yumxpress.pojo.ProductPojo;

/**
 *
 * @author PUSHPRAJ
 */
public class OrderDAO {
//    ORDER ID GENERATION

    public static String getNewId() throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("select max(order_id) from orders");
        rs.next();
        String id = rs.getString(1);
        String orderId = "";
        if (id != null) {
            id = id.substring(4);
            orderId = "ORD-" + (Integer.parseInt(id) + 1);
        } else {
            orderId = "ORD-101";
        }
        return orderId;
    }

//    PLACING ORDER
    public static String placeOrder(PlaceOrderPojo placeOrder) throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("insert into orders values(?,?,?,?,?,?,?,?)");
        placeOrder.setOrderId(getNewId());
        ps.setString(1, placeOrder.getOrderId());
        ps.setString(2, placeOrder.getProductId());
        ps.setString(3, placeOrder.getCustomerId());
        ps.setString(4, placeOrder.getDeliveryStaffId());
        ps.setString(5, ""); //KOI REVIEW SET NAHI HORAHA HAI BCOS ABHI ORDER SIRF PLACE HUA HAI DELIVER NAHI HUA HAI
        ps.setString(6, "ORDERED"); //THIS TELLS THAT THE ORDER HAS BEEN PLACED AND NOT ADDED IN CART
        ps.setString(7, placeOrder.getCompanyId());
        Random rd = new Random(); //OTP SEND KRNE KELIYE RANDOM NUMBER GENERATE KRENGE
        int otp = rd.nextInt(10000); //OTP HOGA FROM 0 TO 9999
        ps.setInt(8, otp);
        if (ps.executeUpdate() == 1) {
            return placeOrder.getOrderId();
        }
        return null;
    }

    public static OrderPojo getOrderDetailsByOrderId(String orderId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String qry = "SELECT c.customer_name, c.address, s.staff_name, c.mobile_no, co.company_name,co.email_id, p.product_name, p.product_price, o.otp "
                + "FROM orders o "
                + "JOIN products p ON o.product_id = p.product_id "
                + "JOIN companies co ON o.company_id = co.company_id "
                + "JOIN customers c ON o.customer_id = c.customer_id "
                + "JOIN staff s ON o.staff_id = s.staff_id "
                + "WHERE o.order_id = ?";
        PreparedStatement ps = conn.prepareStatement(qry);
        ps.setString(1, orderId);
        ResultSet rs = ps.executeQuery();
        OrderPojo order = null;
        if (rs.next()) {
            order = new OrderPojo();
            order.setOrderId(orderId);
            order.setCustomerName(rs.getString("customer_name"));
            order.setCustomerAddress(rs.getString("address"));
            order.setDeliveryStaffName(rs.getString("staff_name"));
            order.setCustomerPhoneNo(rs.getString("mobile_no"));
            order.setCompanyName(rs.getString("company_name"));
            order.setCompanyEmailId(rs.getString("email_id"));
            order.setProductName(rs.getString("product_name"));
            order.setProductPrice(rs.getDouble("product_price"));
            order.setOtp(rs.getInt("otp"));
        }
        return order;
    }

    public static List<OrderPojo> getNewOrdersForStaff(String staffId) throws SQLException {
//        System.out.println(staffId);
        Connection conn = DBConnection.getConnection();
        String qry = "SELECT o.order_id, o.otp, p.product_name, p.product_price, c.customer_name, c.address, c.mobile_no "
                + "FROM orders o "
                + "JOIN products p ON o.product_id = p.product_id "
                + "JOIN customers c ON o.customer_id = c.customer_id "
                + "WHERE o.staff_id = ? "
                + "  AND o.status = 'ORDERED' "
                + "ORDER BY o.order_id DESC";

        PreparedStatement ps = conn.prepareStatement(qry);
        ps.setString(1, staffId);
        ResultSet rs = ps.executeQuery();
        List<OrderPojo> orderList = new ArrayList<>();
        OrderPojo order = null;
        
        while (rs.next()) {
            order = new OrderPojo();
            order.setOrderId(rs.getString("order_id"));
            order.setProductName(rs.getString("product_name"));
            order.setProductPrice(rs.getDouble("product_price"));
            order.setCustomerName(rs.getString("customer_name"));
            order.setCustomerAddress(rs.getString("address"));
            order.setCustomerPhoneNo(rs.getString("mobile_no"));
            order.setOtp(rs.getInt("otp"));
            orderList.add(order);
            System.out.println(order);

        }
        return orderList;
    }

    public static boolean confirmOrder(String orderId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("update orders set status='DELIVERED' where order_id=?");
        ps.setString(1, orderId);
        return ps.executeUpdate() == 1;
    }

    public static void cancelOrder(String orderId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("delete from orders where order_id=orderId");
        ps.executeQuery();
    }

    public static List<OrderPojo> getAllOrdersForCompany(String compId) throws SQLException {
        List<OrderPojo> orders = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("select * from orders where company_id=? AND status='DELIVERED'");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            OrderPojo order = new OrderPojo();
            order.setProductName(rs.getString("product_name"));
            order.setProductPrice(rs.getDouble("product_price"));
            order.setCustomerName(rs.getString("customer_name"));
            order.setDeliveryStaffName(rs.getString("staff_name"));
            order.setCustomerAddress(rs.getString("address"));
            order.setReview(rs.getString("review"));

            orders.add(order);
        }
        return orders;
    }

    public static List<OrderPojo> getAllOrdersForCustomer(String customerId) throws SQLException {
        List<OrderPojo> orders = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            OrderPojo order = new OrderPojo();
            order.setOrderId(rs.getString("order_id"));
            order.setProductName(rs.getString("product_name"));
            order.setProductPrice(rs.getDouble("product_price"));
            order.setDeliveryStaffName(rs.getString("delivery_staff"));
            order.setCompanyName(rs.getString("company_name"));
            order.setCustomerAddress(rs.getString("address"));
            order.setReview(rs.getString("review"));

            // Add the order to the list
            orders.add(order);
        }
        return orders;
    }

    public static List<OrderPojo> getOrdersForDelivery(String deliveryStaffId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("select from orders where staff_id=deliveryStaffId AND status='ORDERED'");
        List<OrderPojo> orders = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            OrderPojo order = new OrderPojo();
            order.setOrderId(rs.getString("order_id"));
            order.setProductName(rs.getString("product_name"));
            order.setProductPrice(rs.getDouble("product_price"));
            order.setCustomerName(rs.getString("customer_name"));
            order.setCustomerAddress(rs.getString("address"));
            order.setReview(rs.getString("review"));

            orders.add(order);
        }
        return orders;
    }

    public static List<OrderPojo> getDeliveredOrdersForStaff(String staffId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String qry = "SELECT o.order_id, o.review, p.product_name, p.product_price, c.customer_name, c.address, c.mobile_no "
                + "FROM orders o "
                + "JOIN products p ON o.product_id = p.product_id "
                + "JOIN customers c ON o.customer_id = c.customer_id "
                + "WHERE o.staff_id = ? "
                + "  AND o.status = 'DELIVERED' "
                + "ORDER BY o.order_id DESC";

        PreparedStatement ps = conn.prepareStatement(qry);
        ps.setString(1, staffId);
        ResultSet rs = ps.executeQuery();
        List<OrderPojo> orderList = new ArrayList<>();
        OrderPojo order = null;
        while (rs.next()) {
            order = new OrderPojo();
            order.setOrderId(rs.getString("order_id"));
            order.setProductName(rs.getString("product_name"));
            order.setProductPrice(rs.getDouble("product_price"));
            order.setCustomerName(rs.getString("customer_name"));
            order.setCustomerAddress(rs.getString("address"));
            order.setCustomerPhoneNo(rs.getString("mobile_no"));
            order.setReview(rs.getString("review"));
            orderList.add(order);

        }
        return orderList;
    }

//  NEW CODE FOR ADDING ITEMS IN CART
    public static boolean addToCart(PlaceOrderPojo order) throws SQLException {
        PreparedStatement ps = DBConnection.getConnection().prepareStatement("insert into orders values(?,?,?,?,?,?,?,?)");
        order.setOrderId(getNewId());
        ps.setString(1, order.getOrderId());
        ps.setString(2, order.getProductId());
        ps.setString(3, order.getCustomerId());
        ps.setString(4, "");
        ps.setString(5, "");
        ps.setString(6, "CART");
        ps.setString(7, order.getCompanyId());
        ps.setInt(8, -1);
        if (ps.executeUpdate() == 1) {
            return true;
        }
        return false;
    }
    
    public static List<CartPojo> getCartDetailsByCustomerId(String customerId)throws SQLException,IOException{
//        System.out.println(customerId+"3");
        Connection conn = DBConnection.getConnection();
        String qry = "SELECT o.order_id, p.product_image,p.company_id, p.product_name, p.product_price, c.customer_name, c.address, c.mobile_no "
                + "FROM orders o "
                + "JOIN products p ON o.product_id = p.product_id "
                + "JOIN customers c ON o.customer_id = c.customer_id "
                + "WHERE o.customer_id = ? "
                + "  AND o.status = 'CART' "
                + "ORDER BY o.order_id DESC";

        PreparedStatement ps = conn.prepareStatement(qry);
        ps.setString(1, customerId);
        ResultSet rs = ps.executeQuery();
        List<CartPojo> cartList = new ArrayList<>();
        CartPojo cart = null;
        while (rs.next()) {
            cart = new CartPojo();
            cart.setOrderId(rs.getString("order_id"));
            cart.setProductName(rs.getString("product_name"));
            cart.setProductPrice(rs.getDouble("product_price"));
            cart.setCompanyId(rs.getString("company_id"));
            InputStream is = rs.getBinaryStream("product_image");
            BufferedImage bi = ImageIO.read(is);
            Image img = bi;
            
            cart.setProductImage(img);
            cartList.add(cart);

        }
        return cartList;
    }

    public static boolean deleteOrderById(String orderId)throws SQLException{
        PreparedStatement ps = DBConnection.getConnection().prepareStatement("delete from orders where order_id=?");
        ps.setString(1, orderId);
        if (ps.executeUpdate() == 1) {
            return true;
        }
        return false;
    }
    
    public static boolean placeOrderFromCart(String staffId,String orderId) throws SQLException {
        PreparedStatement ps = DBConnection.getConnection().prepareStatement("update orders set status='ORDERED',staff_id=?,otp=? where order_id=?");
        
        ps.setString(1, staffId);
        ps.setString(3, orderId);
        
        Random rd = new Random(); //OTP SEND KRNE KELIYE RANDOM NUMBER GENERATE KRENGE
        int otp = rd.nextInt(10000); 
        ps.setInt(2, otp);
        if (ps.executeUpdate() == 1) {
            return true;
        }
        return false;
    }
    
    public static List<OrderPojo> getOrderDetailsByCustomerId(String customerId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String qry = "select \n" +
                     "o.order_id,p.product_name,p.product_price,s.staff_name,c.address \n" +
                     "from orders o\n" +
                     "JOIN products p on o.product_id=p.product_id\n" +
                     "JOIN staff s on o.staff_id=s.staff_id\n" +
                     "JOIN customers c on o.customer_id=c.customer_id\n" +
                     "where o.customer_id=?\n" +
                     "and o.status='ORDERED'";
        PreparedStatement ps = conn.prepareStatement(qry);
        ps.setString(1, customerId);
        ResultSet rs = ps.executeQuery();
        OrderPojo order = null;
        List<OrderPojo> orderList= new ArrayList<OrderPojo>();
        while(rs.next()) {
            order = new OrderPojo();
            order.setOrderId(rs.getString("order_id"));
            order.setProductName(rs.getString("product_name"));
            order.setProductPrice(Double.parseDouble(rs.getString("product_price")));
            order.setDeliveryStaffName(rs.getString("staff_name"));
            order.setCustomerAddress(rs.getString("address"));
            orderList.add(order);
        }
        return orderList;
    }
    
    public static boolean addReviewToOrders(String orderId,String review)throws SQLException{
        PreparedStatement ps = DBConnection.getConnection().prepareStatement("update orders set review=? where order_id=? and status = 'DELIVERED'");
        ps.setString(1, review);
        ps.setString(2, orderId);
        if (ps.executeUpdate() == 1) {
            return true;
        }
        return false;
    }
    
    //    OrderHistory Details
    public static List<OrderPojo> getOrderHistoryDetailsByCustomerId(String customerId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String qry = "select \n" +
                     "o.order_id,p.product_name,p.product_price,s.staff_name,cs.company_name,c.address,o.review \n" +
                     "from orders o\n" +
                     "JOIN products p on o.product_id=p.product_id\n" +
                     "JOIN staff s on o.staff_id=s.staff_id\n" +
                     "JOIN customers c on o.customer_id=c.customer_id\n" +
                     "JOIN companies cs on o.company_id=cs.company_id\n" +
                     "where o.customer_id=?\n" +
                     "and o.status='DELIVERED'";
        PreparedStatement ps = conn.prepareStatement(qry);
        ps.setString(1, customerId);
        ResultSet rs = ps.executeQuery();
        OrderPojo order = null;
        List<OrderPojo> orderList= new ArrayList<OrderPojo>();
        while(rs.next()) {
            order = new OrderPojo();
            order.setOrderId(rs.getString("order_id"));
            order.setProductName(rs.getString("product_name"));
            order.setProductPrice(Double.parseDouble(rs.getString("product_price")));
            order.setDeliveryStaffName(rs.getString("staff_name"));
            order.setCompanyName(rs.getString("company_name"));
            order.setCustomerAddress(rs.getString("address"));
            order.setReview(rs.getString("review"));
            orderList.add(order);
        }
        return orderList;
    }
    
//    ORDER LIST FOR COMPANY
    public static List<OrderPojo> getOrderListByCompanyId(String companyId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String qry = "select \n" +
                     "o.order_id,p.product_name,p.product_price,s.staff_name,c.customer_name,c.address,o.review \n" +
                     "from orders o\n" +
                     "JOIN products p on o.product_id=p.product_id\n" +
                     "JOIN staff s on o.staff_id=s.staff_id\n" +
                     "JOIN customers c on o.customer_id=c.customer_id\n" +
                     "JOIN companies cs on o.company_id=cs.company_id\n" +
                     "where o.company_id=?\n" +
                     "and o.status !='CART'";
        PreparedStatement ps = conn.prepareStatement(qry);
        ps.setString(1, companyId);
        ResultSet rs = ps.executeQuery();
        OrderPojo order = null;
        List<OrderPojo> orderList= new ArrayList<OrderPojo>();
        while(rs.next()) {
            order = new OrderPojo();
            order.setOrderId(rs.getString("order_id"));
            order.setProductName(rs.getString("product_name"));
            order.setProductPrice(Double.parseDouble(rs.getString("product_price")));
            order.setDeliveryStaffName(rs.getString("staff_name"));
            order.setCustomerAddress(rs.getString("address"));
            order.setCustomerName(rs.getString("customer_name"));
            order.setReview(rs.getString("review"));
            orderList.add(order);
        }
        return orderList;
    }
}
