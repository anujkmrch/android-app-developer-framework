package my.academic.design.App;

import android.content.Context;
import android.content.SharedPreferences;

import my.academic.design.Models.User;

/**
 * Centralize App Manager class to access centralize application feature
 */

public class AppStorage {
    private String TAG = AppStorage.class.getSimpleName();

    SharedPreferences preference;

    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREFERENCE_NAME = Environment.PREFERNCE_NAME;

    // All Shared preferenceerences Keys
    private static final String KEY_USER_ID = "live_token";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_USER_AVATAR = "avatar";
    private static final String KEY_USER_TYPE = "type";
    private static final String KEY_CHATSPACE = "username";


    private static final String KEY_SPACE = "space";

    // Constructor
    public AppStorage(Context context)
    {
        this._context = context;
        preference = _context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        editor = preference.edit();
    }

    public void storeUser(User user)
    {

        editor.putString(KEY_USER_ID, user.getId());

        editor.putString(KEY_USER_NAME, user.getName());

        editor.putString(KEY_USER_EMAIL, user.getEmail());

//        editor.putString(KEY_USER_AVATAR, user.getAvatar());

        editor.putString(KEY_USER_TYPE, user.getType());

        editor.putString(KEY_CHATSPACE, user.getUsername());

        editor.commit();

//        Log.e(TAG, "User is stored in shared preferenceerences. " + user.getName() + ", " + user.getEmail() + ", " + user.getType()+ ", " + user.get_chatspace());

    }

    public User getUser() {

        if (preference.getString(KEY_USER_ID, null) != null)
        {

            String id, name, email,avatar,_type,username;

            id = preference.getString(KEY_USER_ID, null);

            name = preference.getString(KEY_USER_NAME, null);

            email = preference.getString(KEY_USER_EMAIL, null);

            avatar = preference.getString(KEY_USER_AVATAR,null);

            _type = preference.getString(KEY_USER_TYPE,null);

            username = preference.getString(KEY_CHATSPACE,null);

//            User user = new User(id, name, email,avatar,_type,_chatspace);

            User user = new User(id, name, email,username,_type);

            return user;

        }

        return null;
    }

    public void setSpace(String space)
    {

        editor.putString(KEY_SPACE,space);

        editor.commit();

    }

    public String getSpace()
    {
        return preference.getString(KEY_SPACE,"docs");
    }


    public void addNotification(String notification)
    {
        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications()
    {
        return preference.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear()
    {

        editor.clear();

        editor.commit();

    }
}