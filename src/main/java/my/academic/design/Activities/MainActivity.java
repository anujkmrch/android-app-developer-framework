package my.academic.design.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import my.academic.design.App.Environment;
import my.academic.design.App.Functionality;
import my.academic.design.App.Project;
import my.academic.design.App.Utils;
import my.academic.design.Models.User;
import my.academic.design.R;


public class MainActivity extends AppCompatActivity {

    private TextView tvId,tvName, tvRole;

    public static Integer REQUEST_LOGIN = 1;
    public static Integer REQUEST_REGISTER = 2;
    public static Integer REQUEST_AUTHENTICATION = 3;
    public static Integer REQUEST_LOGOUT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Environment.allowOnlyIfHasInternetAccess && !Utils.isNetworkAvailable(this))
        {
            Log.d("CHK NET ACCESS","Internet Access check");
            Toast.makeText(this, "No Network Available", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("No Internet");
            builder.setMessage("Internet is required. Please Retry.");

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    //InitiateDownload();
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);

                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
            AlertDialog dialog = builder.create(); // calling builder.create after adding buttons
            dialog.show();
        }

        /**
         * It checks the login everytime, but it needs to turn the situation on
         */
        if(Environment.checkLoginEveryTime)
        {
            Log.d("CHK LOGIN ALWAYS","If Application needs to check user login from the server al the time");
        }

        /**
         * In case of user is not logged in and you want user to login to access the account in this case
         * it will ask user to login always other wise if user is logged or there is no limitation for guest user
         * then they can view the content
         */
        if(Project.getInstance().getPref().getUser()== null && Environment.forceLogin)
        {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "I do not have any user and forced to login", Toast.LENGTH_SHORT).show();
        }
        else if(Project.getInstance().getPref().getUser()== null){ //user is not found in preference object
            Toast.makeText(this, "I am not the user and i am not forced to login for actions", Toast.LENGTH_LONG).show();
        }
        else
        {
            setContentView(R.layout.activity_profile);

            User u = Project.getInstance().getPref().getUser();

            tvId = (TextView) findViewById(R.id.profileUsername);
            tvName = (TextView) findViewById(R.id.profileName);
            tvRole = (TextView) findViewById(R.id.profileRole);

            tvId.setText(u.getUsername());
            tvName.setText(u.getName());
            tvRole.setText(u.getType());

            String set1 = "1_1.png";

            String set2 = "2_1.png";
            String set4 = "4_1.png";

            Toast.makeText(this, getFilesDir().toPath().toString(), Toast.LENGTH_SHORT).show();

            try{
                String basePath = getFilesDir().toPath().toString();

                    int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

                    if(result != PackageManager.PERMISSION_GRANTED)
                    {
                        try {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},5);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw e;
                        }
                    }

                String path = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS).toString();
                Bitmap bitset1 = BitmapFactory.decodeFile(path+'/'+set1);

                Bitmap bitset2 = BitmapFactory.decodeFile(basePath+set2);
                Bitmap bitset4 = BitmapFactory.decodeFile(basePath+set4);

                Functionality.SetFingerPrint(this,bitset1,8);

            } catch(Exception e)
            {
                e.printStackTrace();
            }

//
//          File directory = getFilesDir();
//          File file = new File(directory, "1_1.png");
//
//          String match1 = "1_1.png";
//          String match2 = "2_1.png";
//          String match3 = "3_1.png";
//          String match4 = "4_1.png";
//
//          Bitmap bitmatch1 = BitmapFactory.decodeFile(match1);
//          Bitmap bitmatch2 = BitmapFactory.decodeFile(match2);
//          Bitmap bitmatch3 = BitmapFactory.decodeFile(match3);
//          Bitmap bitmatch4 = BitmapFactory.decodeFile(match4);

//          Functionality.MatchFingerPrint(this,bitmatch1,false);

        }
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem login = menu.findItem(R.id.login);
        MenuItem register = menu.findItem(R.id.register);
        MenuItem logout = menu.findItem(R.id.logout);
        MenuItem authenticate = menu.findItem(R.id.authenticate);
        MenuItem report = menu.findItem(R.id.report);
        MenuItem feedback = menu.findItem(R.id.feedback);
        MenuItem fingerPrint = menu.findItem(R.id.uploadFingerPrint);
        fingerPrint.setVisible(false);
        if(Project.getInstance().getPref().getUser()==null) //enable or diable menu item in case of user is logged out
        {
            authenticate.setVisible(false);
            logout.setVisible(false);
            report.setVisible(false);

        } else //enable or diable menu item in case of user is logged in
        {
            login.setVisible(false);
            register.setVisible(false);

            if (!Project.getInstance().getPref().getUser().getType().equals("cordinator")) {
                authenticate.setVisible(false);
                report.setVisible(false);
                feedback.setVisible(false);
            }

            if (!Project.getInstance().getPref().getUser().getType().equals("administrator")) {
                fingerPrint.setVisible(false);
            }
        }
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.login:
                intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(intent,REQUEST_LOGIN);
                return true;

            case R.id.register:
                intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivityForResult(intent,REQUEST_REGISTER);
                return true;

            case R.id.authenticate:
                intent = new Intent(MainActivity.this,AuthenticationActivity.class);
                startActivityForResult(intent,REQUEST_AUTHENTICATION);
                return true;

//            case R.id.report:
//                intent = new Intent(MainActivity.this,ReportActivity.class);
//                startActivityForResult(intent,REQUEST_AUTHENTICATION);
//                return true;

            case R.id.profile:
                intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivityForResult(intent,REQUEST_AUTHENTICATION);
                return true;


//            case R.id.feedback:
//                intent = new Intent(MainActivity.this,FeedbackActivity.class);
//                startActivityForResult(intent,REQUEST_AUTHENTICATION);
//                return true;

            case R.id.logout:
                intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Project.getInstance().logout();
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
