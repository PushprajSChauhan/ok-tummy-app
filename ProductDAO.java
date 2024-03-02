/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yumxpress.dao;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import yumxpress.dbutil.DBConnection;
import yumxpress.pojo.ProductPojo;

/**
 *
 * @author PUSHPRAJ
 */
public class ProductDAO {
//    AUTO GENERATING NEW PRODUCT ID
    public static String getNewId() throws SQLException{
        Connection conn=DBConnection.getConnection();
        Statement st=conn.createStatement();
        ResultSet rs=st.executeQuery("select max(product_id) from products");
        rs.next();
        String id=rs.getString(1);
        String prodId="";
        if(id!=null){
            id=id.substring(4);
            prodId="PRD-"+(Integer.parseInt(id)+1);
        }
        else{
            prodId="PRD-101";
        }
        return prodId;
    }
    
//    ADDING NEW PRODUCT IN COMPANY'S MENU
    public static boolean addProduct(ProductPojo product) throws SQLException, IOException {
//      CONVERT IMAGE TO BUFFERED IMAGE
        BufferedImage bufferedImage=new BufferedImage(product.getProductImage().getWidth(null),product.getProductImage().getHeight(null),BufferedImage.TYPE_INT_RGB);
//      ISS LINE SE SERVER SE AYI IMAGE KO CONVERT KRENGE BUFFERED IMAGE MEI JISSE FIR BYTE ARRAY BANAKE AAGE KAAM KRENGE 

//      DRAWING THE IMAGE INSIDE BUFFERED IMAGE OBJECT     
        Graphics gfx=bufferedImage.getGraphics();
        gfx.drawImage(product.getProductImage(),0,0,null);
//      GRAPHICS CLASS KA OBJECT DRAW KREGA IMAGE KO AS RGB IMAGE ON BUFFERED IMAGE KA BLANK OBJECT...AND AB IMAGE GFX MEI BHI AND IMAGE OBJECT MEI BHI RHEGI

//      CONVERT BUFFEREDIMAGE TO BYTES ARRAY
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
//      BYTEARRAYOUTPUTSTREAM CLASS BYTE ARRAY KA OBJECT REPRESENTATION HAI JAISE INTEGER WRAPPER CLASS INT PRIMITIVES KA OBJECT REPRESENTATION HAI  
        ImageIO.write(bufferedImage, product.getProductImageType(), baos);
//      YEH CLASS BUFFERED IMAGE KO BYTES ARRAY MEI CONVERT KREGI 
        byte [] imageData=baos.toByteArray();
        ByteArrayInputStream bais=new ByteArrayInputStream(imageData);
        
//      SENDING THE DATA TO THE DB
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("insert into products values(?,?,?,?,?)");
        ps.setString(1,getNewId());
        ps.setString(2,product.getCompanyId());
        ps.setString(3,product.getProductName());
        ps.setDouble(4,product.getProductPrice());
        ps.setBinaryStream(5,bais,imageData.length);
        
        int x=ps.executeUpdate();
        return x>0;
        
//      (1)WE FIRST CONVERT THE IMAGE OBJECT RECEIVED FROM API INTO BUFFERED IMAGE OBJJCT BCOS BUFFERED IMAGE USES RAM BUFFERS AND SO IS MUCH FAST AS COMPARED TO RAW IMAGE OBJECTS

//      (2)WE THEN DRAW THE IMAGE OBJECT INSIDE BUFFERED IMAGE OBJECT USNG THE CLASS GRAPHICS BY CALLING GRAPHICS METHOD CALLED DRAWIMAEG()

//      (3)SINCE WE CANNOT WRITE BUFFEREDIMAGE IN DB SO WE CONVERT IT INTO BYTES ARRAY. THIS IS DONE WITH THE HELP OF 2 CLASSES WHICH ARE BYTEARRAYOUTPUTSTREAM AND IMAGEIO
//         SINCE WE GET A BYTEARRAYOUTPUTSTREAM OBJECT THE NEXT TASK IS TO CONVERT IT INTO BYTE ARRAY AND THIS IS DONE USING THE METHOD TOBYTEARRAY OF BYTEARRAYOUTPUTSTREAM CLASS

//      (4)WE THEN CONVERT THIS BYTE ARRAY INTO OBJECT OF BYTEARRAYINPUTSTREAM CLASS BCOS THE METHOD SETBINARYSTREAM OF PREPAREDSTATEMENT OBJECT DOES NOT DIRECTLY ACCEPT A BYTE ARRAY RATHER IT WANTS A INPUTSTREAM OBJECT.
//         SINCE BYTEARRAYINPUTSTREAMIS CHILD CLASS OF INPUTSTREAM WE CAN PASS ITS OBJECT AS ARGUMENT TO SETBINARYSTREAM METHOD
    }
    
//    FOR DISPLAYING THE IMAGE IN VIEW FOOD FRAME
    public static Map<String,ProductPojo> getProductDetailsByCompanyId(String companyId) throws SQLException,IOException{
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps=conn.prepareStatement("select * from products where company_id=?");
        ps.setString(1, companyId);
        ResultSet rs=ps.executeQuery();
        Map<String,ProductPojo> productDetails=new HashMap<>();
        while(rs.next()){
            ProductPojo product=new ProductPojo();
            product.setProductName(rs.getString(3));
            product.setProductPrice(rs.getDouble(4));
            InputStream inputStream=rs.getBinaryStream("product_image");
            
//            CONVERT INPUTSTREAM TO BUFFEREDIMAGE
            BufferedImage bufferedImage=ImageIO.read(inputStream);
            
//            CONVERT BUFFEREDIMAGE TO IMAGE
            Image image=bufferedImage;
            product.setProductImage(image);
            productDetails.put(product.getProductName(), product);
        }
        return productDetails;
    }
    
    
   public static List<ProductPojo> getAllProductsByCompanyId(String compId) throws SQLException,IOException{
//    ISS METHOD SE SELECTED COMPANY KE NAAM SE ID LEKE COMPANY KE PRODUCTS KI SAARI DETAILS LELENGE TO BE DISPLAYED ON FRAME   
//    YAHA SE GETPRODUCTDETAILSBYCOMPANYID WALA METHOD CALL KRDENGE, JO MAP RETURN KREGA AND USS MAP KI VALUES KA ARRAY LIST YA COLLECTION BANAKE HAM USSE YAHA SE RETURN KRDENGE
//    BUT HAME USS METHOD SE AUR DETAILS CHIYE HAI ISLIYE USME CHERD KARNE KI JAGAH NAYA IMPLEMENTATION KRENGE YAHA PE
        Connection conn=DBConnection.getConnection();
        PreparedStatement ps;
        if(compId.equalsIgnoreCase("ALL")){
            ps=conn.prepareStatement("select * from products where company_id in (select company_id from companies where status='ACTIVE')");
        }else{
            ps=conn.prepareStatement("select * from products where company_id=?");
            ps.setString(1, compId);
        }
        ResultSet rs=ps.executeQuery();
        List<ProductPojo> productDetails=new ArrayList<>();
        while(rs.next()){
            ProductPojo product=new ProductPojo();
            product.setCompanyId(rs.getString("company_id"));
            product.setProductId(rs.getString("product_id"));
            product.setProductName(rs.getString("product_name"));
            product.setProductPrice(rs.getDouble("product_price"));
            InputStream inputStream=rs.getBinaryStream("product_image");
            
//            CONVERT INPUTSTREAM TO BUFFEREDIMAGE
            BufferedImage bufferedImage=ImageIO.read(inputStream);
            
//            CONVERT BUFFEREDIMAGE TO IMAGE
            Image image=bufferedImage;
            product.setProductImage(image);
            productDetails.add(product);
        }
        return productDetails;
   }
}
