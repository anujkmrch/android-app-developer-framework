package my.academic.design.App;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import my.academic.design.Activities.MainActivity;

/**
 * Project application instance
 */

public class Project extends Application {
    private static Project mInstance;
    private RequestQueue mRequestQueue;
    private AppStorage pref;



    public static final String TAG = Project.class.getSimpleName();


    @Override
    public void onCreate(){ super.onCreate(); mInstance = this; }

    public static synchronized Project getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void logout() {
        pref.clear();

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

    }

    public AppStorage getPref() {
        if (pref == null) {
            pref = new AppStorage(this);
        }
        return pref;
    }

    public Uri.Builder getUriBuilder(){
        Uri.Builder urlBuilder = new Uri.Builder();
        return urlBuilder;
    }

    public Uri.Builder getBaseUrl(boolean include_api, boolean include_token){
        Uri.Builder b = Project.getInstance().getUriBuilder();
        b.scheme(Environment.SCHEME);
        b.encodedAuthority(Environment.DOMAIN);
        if(include_api==true)
            b.appendEncodedPath(Environment.API_PREFIX);
        if(include_token==true)
            b.appendQueryParameter("token", Project.getInstance().getPref().getUser().getId());
        return b;
    }
}
