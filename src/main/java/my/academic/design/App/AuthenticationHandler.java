package my.academic.design.App;

/**
 * Created by anuj on 10/03/18.
 */
import android.hardware.fingerprint.FingerprintManager;
import android.widget.Toast;

import my.academic.design.Activities.MainActivity;
import my.academic.design.Activities.ProfileActivity;

public class AuthenticationHandler extends FingerprintManager.AuthenticationCallback{
    private ProfileActivity mainActivity;
    public AuthenticationHandler(ProfileActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        Toast.makeText(mainActivity, "Auth Error: "+errString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        Toast.makeText(mainActivity, "Auth Help: "+helpString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        Toast.makeText(mainActivity, "Auth Succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Toast.makeText(mainActivity, "Auth Failed", Toast.LENGTH_SHORT).show();
    }
}
