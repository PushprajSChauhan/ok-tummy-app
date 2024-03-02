/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yumxpress.util;

import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 * @author PUSHPRAJ
 */
public class Validator {
    public static boolean isValidEmail(String emailId){
        EmailValidator validator=EmailValidator.getInstance();
        return validator.isValid(emailId);
    }
    
    public static boolean isValidMobileNo(String mobileNo){
        if(mobileNo.length()!=10){
            return false;
        }
        char [] mobNoArr=mobileNo.toCharArray();
        for(char ch:mobNoArr){
            if(Character.isDigit(ch)==false){
                return false;
            }
        }
        return true;
    }
}
