package com.codeworm.barkapp;

/**
 * Created by Harvie Marcelino on 01/11/2018.
 */

public class Constants {
//    private static final String ROOT_URL = "http://192.168.0.10/Marcelino-ws-php/BarkApp%20PHP%20Script/";
//    private static final String ROOT_URL = "http://192.168.43.170/Marcelino-ws-php/BarkApp%20PHP%20Script/";
    private static final String ROOT_URL = "https://barkapp.000webhostapp.com/BarkApp%20PHP%20Script/";

    public static final String URL_LOGIN = ROOT_URL+"loginUser.php";
    public static final String URL_REGISTER = ROOT_URL+"createUser.php";
    public static final String URL_CHECK_USERNAME = ROOT_URL+"checkUsername.php";
    public static final String URL_SCANNED_QR = ROOT_URL+"scanQR.php";
    public static final String URL_PARKING_DETAILS = ROOT_URL+"checkParkingDetails.php";
    public static final String URL_CHECK_PARKING_STATUS = ROOT_URL+"checkParkingStatus.php";
    public static final String URL_UPDATE_GENERAL_LOG = ROOT_URL+"updateGeneralLog.php";
    public static final String URL_GET_PARKING_LOGS = ROOT_URL+"getParkingLogs.php";
    public static final String URL_SEND_CODE = ROOT_URL+"sendSMS.php";
    public static final String URL_CHECK_CODE = ROOT_URL+"checkCode.php";
    public static final String URL_CHECK_FORGOT_PASSWORD = ROOT_URL+"checkPreReqForgotPassword.php";
    public static final String URL_CHANGE_PASSWORD = ROOT_URL+"changePassword.php";
    public static final String URL_CHANGE_USERNAME = ROOT_URL+"changeUsername.php";
    public static final String URL_CHANGE_MOBILE_NUMBER = ROOT_URL+"changeMobileNumber.php";
    public static final String URL_CHECK_USER_STATUS = ROOT_URL+"checkUserStatus.php";
    public static final String URL_CHANGE_FULLNAME = ROOT_URL+"changeFullname.php";
    public static final String URL_GET_USER_INFORMATION_DETAILS = ROOT_URL+"getUserInformationDetails.php";

    public static final String UPDATE_GENERAL_LOG_UPDATE = "update";
    public static final String UPDATE_GENERAL_LOG_REMOVE = "remove";
}
