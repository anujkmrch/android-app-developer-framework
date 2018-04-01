package my.academic.design.App;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import my.academic.design.Activities.MainActivity;
import my.academic.design.Activities.ReportActivity;
import my.academic.design.Models.User;

/**
 * Functionality class
 */

public class Functionality {
    /**
     *
     * @param context
     * @param username
     * @param password
     */
    public static void Login(final Context context,String username,String password)
    {
        Map<String,String> map = new HashMap<String,String>();
        map.put("username",username.toString());
        map.put("password",password.toString());
        JSONObject inputs = new JSONObject(map);
        String _url = Project.getInstance().getBaseUrl(true,false).appendEncodedPath(Environment.LOGIN).toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,_url, inputs, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    //if has status variable and it is true then the successfully got the result else
                    // there is some error occurred with request
                    if(response.has("success") && response.getBoolean("success"))
                    {
                        JSONObject data = response.getJSONObject("data");
                        try{
                            if(data.has("token") && data.has("user")) {

                                JSONObject u = data.getJSONObject("user");

                                User user = new User(data.getString("token"),u.getString("name"),u.getString("email"),u.optString("username"),u.optString("type"));

                                Project.getInstance().getPref().storeUser(user);

                                Intent intent = new Intent(context,MainActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);

                                context.startActivity(intent);

                            }
                        } catch (JSONException exe)
                        {
                            exe.printStackTrace();
                            Toast.makeText(context, "Invalid Response Type, Inner Try", Toast.LENGTH_SHORT).show();

                        }
                    }

                    if(response.has("error") && response.getBoolean("error"))
                    {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException ex){
                    Log.d("Reposnse type",response.toString());
                    Toast.makeText(context, "Invalid Response Type, Please correct the response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error response",error.toString());
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        Project.getInstance().getRequestQueue().add(jsonObjectRequest);

    }

    /**
     *
     * @param context
     * @param username
     * @param password
     */
    public static void Register(final Context context,String username,String password,String email,String fname,String lname)
    {
        Map<String,String> map = new HashMap<String,String>();
        map.put("username",username.toString());
        map.put("password",password.toString());
        map.put("email",email.toString());
        map.put("first_name",fname.toString());
        map.put("last_name",lname.toString());
        JSONObject inputs = new JSONObject(map);
        String _url = Project.getInstance().getBaseUrl(true,false).appendEncodedPath(Environment.REGISTER).toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,_url, inputs, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    //if has status variable and it is true then the successfully got the result else
                    // there is some error occurred with request
                    if(response.has("success") && response.getBoolean("success"))
                    {

                    }
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException ex){
                    Log.d("Reposnse type",response.toString());
                    Toast.makeText(context, "Invalid Response Type, Please correct the response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        Project.getInstance().getRequestQueue().add(jsonObjectRequest);
    }

    //method for matching the fingerprint from fingerprint recognition server
    public static void MatchFingerPrint(final Context context, final Bitmap bmp,final boolean up)
    {
        JSONObject matchResult;
        Toast.makeText(context, "I AM IN TESTING FUNCTIONALITY", Toast.LENGTH_SHORT).show();
        Map<String,String> map = new HashMap<String,String>();
        map.put("fingerprint",bmp.toString());
        JSONObject inputs = new JSONObject(map);
        String _url = Environment.IDENTIFICATIONURL;
        Toast.makeText(context, _url, Toast.LENGTH_SHORT).show();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, _url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    boolean status = result.getBoolean("success");
                    String message = result.getString("message");

                    if (status) {
                        // tell everybody you have succed upload image and post strings
                        Log.i("Messsage", message);
                    } else {
                        Log.i("Unexpected", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        boolean status = response.getBoolean("success");
                        String message = response.getString("message");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                params.put("file", new DataPart("file_cover.jpg", byteArray));

                // params.put("avatar", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mAvatarImage.getDrawable()), "image/jpeg"));
                // params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));

                return params;
            }
        };

        Project.getInstance().getRequestQueue().add(volleyMultipartRequest);

//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,_url, inputs, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try{
//                    // if user not found, take me to the report activity
//                    if(response.has("error") && response.getBoolean("error"))
//                    {
//                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, ReportActivity.class);
//                        context.startActivity(intent);
//                    }
//
//                    //if user is found then mark the attendance automatically
//                    if(response.has("success") &&  response.getBoolean("success"))
//                    {
//                        Toast.makeText(context, "Succeffuly found marking attendence", Toast.LENGTH_SHORT).show();
//                        Map<String,String> map = new HashMap<String,String>();
//                        map.put("username",response.getString("collegeid"));
//                        String _url = Project.getInstance().getBaseUrl(true,false).appendEncodedPath(Environment.ATTENDANCE).toString();
//                        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, _url, null, new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try{
//
//                                    Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
//
//                                } catch(Exception e){
//                                    e.getMessage();
//                                }
//                            }
//                        },null);
//
//                    }
//                } catch (JSONException ex){
//                    Toast.makeText(context, "Invalid Response Type, Please correct the response", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        Project.getInstance().getRequestQueue().add(jsonObjectRequest);

    }

    //method for setting the fingerprint to the fingerprint recognition server
    public static void SetFingerPrint(final Context context, final Bitmap bmp,final Integer id)
    {
        JSONObject matchResult;
        Toast.makeText(context, "I AM IN TESTING FUNCTIONALITY", Toast.LENGTH_SHORT).show();

//        Map<String,String> map = new HashMap<String,String>();
//        map.put("fingerprint",bmp.toString());
//
//        JSONObject inputs = new JSONObject(map);

        String _url = Environment.IDENTIFICATIONURL;

        Toast.makeText(context, _url, Toast.LENGTH_SHORT).show();
        //volley multipart request

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, _url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
//                try {
//                    JSONObject result = new JSONObject(resultResponse);
//                    boolean status = result.getBoolean("success");
//                    String message = result.getString("message");

                    Log.d("Set message",resultResponse.toString());

//                    if (status) {
//                        // tell everybody you have succed upload image and post strings
//                        Log.i("Messsage", message);
//                    } else {
//                        Log.i("Unexpected", message);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        boolean status = response.getBoolean("success");
                        String message = response.getString("message");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("collegeid", id.toString());
                return params;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Log.d("Datapart Stream",stream.toString());

              params.put("myfile", new DataPart("file.png", byteArray));

                return params;
            }
        };
        
        Project.getInstance().getRequestQueue().add(volleyMultipartRequest);

//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,_url, inputs, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try{
//                    // if user not found, take me to the report activity
//                    if(response.has("error") && response.getBoolean("error"))
//                    {
//                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, ReportActivity.class);
//                        context.startActivity(intent);
//                    }
//
//                    //if user is found then mark the attendance automatically
//                    if(response.has("success") &&  response.getBoolean("success"))
//                    {
//                        Toast.makeText(context, "Succeffuly found marking attendence", Toast.LENGTH_SHORT).show();
//                        Map<String,String> map = new HashMap<String,String>();
//                        map.put("username",response.getString("collegeid"));
//                        String _url = Project.getInstance().getBaseUrl(true,false).appendEncodedPath(Environment.ATTENDANCE).toString();
//                        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, _url, null, new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try{
//
//                                    Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
//
//                                } catch(Exception e){
//                                    e.getMessage();
//                                }
//                            }
//                        },null);
//
//                    }
//                } catch (JSONException ex){
//                    Toast.makeText(context, "Invalid Response Type, Please correct the response", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        Project.getInstance().getRequestQueue().add(jsonObjectRequest);

    }
}
