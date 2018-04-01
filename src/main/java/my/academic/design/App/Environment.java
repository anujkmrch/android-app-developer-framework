package my.academic.design.App;

/**
 * Configuration environment class
 */

public class Environment {

    /*
     *  Enable if you want to serve app for only logged in user
     */
    public static final Boolean forceLogin = true;

    public static final String notificationTitle = "I AM THE NOTIFICATION TITLE";

    public static final Boolean allowOnlyIfHasInternetAccess = true;

    public static final Boolean checkLoginEveryTime = false;

    public static final String APP_NAMESPACE = "myempress";

    public static final String PREFERNCE_NAME = "myempress_pref";

    public static final String SCHEME = "http";

    public static final String DOMAIN = "10.0.2.2:8000";

    public static final String API_PREFIX = "api";

    public static final String LOGIN = "login";

    public static final String REGISTER = "register";

    public static final String ATTENDANCE= "mark-attendance";

    public static final String AUTHENTICATE = "authenticate";

    public static final String REPORT = "report";

    public static final String GETUSER = "getUser";

    public static final String LOGINCHECK = "loginCheck";

    public static final String PROFILE = "profile";

    public static final String AVATAR = "avatar";

    public static final String UPLOAD = "upload";

    public static final String SEND_MESSAGE = "send_message";

    public static final String SEND_ATTACHMENT = "send_attachment";

    public static final String PHOTO_URL = "storage";

    public static final String IDENTIFICATIONURL = "http://10.0.2.2:8080/rjs/fingerprint.upload.match";

    public static final String UPLOADFINGERPRINTURL = "http://10.0.2.2:8080/rjs/fingerprint.upload.set";

}