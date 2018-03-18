package com.codeworm.barkapp;

/**
 * Created by Harvie Marcelino on 01/08/2018.
 */

public class ValidationFlag {
    private boolean checkUsernameFlag = false;
    private boolean checkUser = false;
    private boolean validateQr;

    public boolean isCheckUsernameFlag() {
        return checkUsernameFlag;
    }


    public void setCheckUsernameFlag(boolean checkUsernameFlag) {
        this.checkUsernameFlag = checkUsernameFlag;
    }


    public boolean isCheckUser() {
        return checkUser;
    }

    public void setCheckUser(boolean finalDecision) {
        this.checkUser = finalDecision;
    }

    public boolean isValidateQr() {
        return validateQr;
    }

    public void setValidateQr(boolean validateQr) {
        this.validateQr = validateQr;
    }

    public boolean checkMobileNumber(String sNumber){
        boolean error = true;

        if(sNumber.length() == 11){
            if(sNumber.charAt(0) == '0' && sNumber.charAt(1) == '9'){
                error = false;
            }else{
                error = true;
            }
        }
        if(sNumber.length() == 12){
            if(sNumber.charAt(0) == '6' && sNumber.charAt(1) == '3' && sNumber.charAt(2) == '9'){
                error = false;
            }else{
                error = true;
            }
        }

        return error;
    }

    public String formatMobileNumber(String sNumber){
        String formattedNumber = "";

        if(sNumber.charAt(0) == '0' || (sNumber.charAt(0) == '6' && sNumber.charAt(1) == '3')){
            if(sNumber.charAt(0) == '0'){
                String sHolder = "";
                for(int ctr = 1; ctr < sNumber.length(); ctr++){
                    sHolder += sNumber.charAt(ctr);
                }
                formattedNumber = sHolder;
            }else{
                String sHolder = "";
                for(int ctr = 2; ctr < sNumber.length(); ctr++){
                    sHolder += sNumber.charAt(ctr);
                }
                formattedNumber = sHolder;
            }
        }

        return formattedNumber;
    }
}
