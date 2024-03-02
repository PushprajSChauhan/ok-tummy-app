/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yumxpress.pojo;

/**
 *
 * @author PUSHPRAJ
 */
public class CustomerPojo {

    @Override
    public String toString() {
        return "CustomerPojo{" + "name=" + customerName + ", emailId=" + emailId + ", password=" + password + ", mobileNo=" + mobileNo + ", address=" + address + ", customerId=" + customerId + '}';
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCustomerId(){
        return customerId;
    }
    
    public void setCustomerId(String customerId){
        this.customerId=customerId;
    }

    public CustomerPojo(String name, String emailId, String password, String mobileNo, String address, String customerId) {
        this.customerName = name;
        this.emailId = emailId;
        this.password = password;
        this.mobileNo = mobileNo;
        this.address = address;
        this.customerId=customerId;
    }

    public CustomerPojo() {
    }
    
    private String customerId;
    private String customerName;
    private String emailId;
    private String password;
    private String mobileNo;
    private String address;
}
