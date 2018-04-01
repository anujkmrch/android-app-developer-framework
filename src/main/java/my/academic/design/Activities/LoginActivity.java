package my.academic.design.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import my.academic.design.App.Functionality;
import my.academic.design.R;

public class LoginActivity extends AppCompatActivity {
    public EditText loginkey,password;
    public TextView register_link,forgot_link, loginBtn;
    public static Integer REQUEST_REGISTER = 22;
    public static Integer REQUEST_FORGOTPASSWORD = 44;

    /**
     *
     * @param savedInstanceState
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginkey = (EditText) findViewById(R.id.loginKey);
        password = (EditText) findViewById(R.id.loginPassword);
        register_link = (TextView) findViewById(R.id.register);
        forgot_link = (TextView) findViewById(R.id.tvforgotPassword);
        loginBtn = (TextView) findViewById(R.id.loginBtn);
    }

    /**
     *
     * @param view
     *
     */
    public void login(View view)
    {
        if(loginkey.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter login id", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!loginkey.getText().toString().isEmpty() && password.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(loginkey.getText().toString().isEmpty() && password.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Missing login id and password", Toast.LENGTH_SHORT).show();
            return;
        }
//        Toast.makeText(this, loginkey.getText().toString()+password.getText().toString(), Toast.LENGTH_SHORT).show();
//        Log.d("Login Check",loginkey.getText().toString()+password.getText().toString());

        Functionality.Login(this,loginkey.getText().toString(),password.getText().toString());
    }

    /**
     *
     * @param view
     *
     */
    public void register(View view)
    {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivityForResult(intent,1);
    }

    /**
     *
     * @param view
     *
     */
    public void forgotpassword(View view)
    {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivityForResult(intent,1);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *
     * @param item
     * @return
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param menu
     * @return
     *
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}