package my.academic.design.Activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;

import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * *****************************************************************************
 * C O P Y R I G H T  A N D  C O N F I D E N T I A L I T Y  N O T I C E
 *
 * Copyright Â© 2008-2009 Access Computech Pvt. Ltd. All rights reserved.
 * This is proprietary information of Access Computech Pvt. Ltd.and is
 * subject to applicable licensing agreements. Unauthorized reproduction,
 * transmission or distribution of this file and its contents is a
 * violation of applicable laws.
 * *****************************************************************************
 *
 * project FM220_Android_SDK
 */

import com.acpl.access_computech_fm220_sdk.FM220_Scanner_Interface;
import com.acpl.access_computech_fm220_sdk.acpl_FM220_SDK;
import com.acpl.access_computech_fm220_sdk.fm220_Capture_Result;
import com.acpl.access_computech_fm220_sdk.fm220_Init_Result;

import my.academic.design.App.Functionality;
import my.academic.design.R;

public class AuthenticationActivity extends AppCompatActivity implements FM220_Scanner_Interface {

    private acpl_FM220_SDK FM220SDK;
    private Button AuthCheck,Capture_PreView;
    private TextView textMessage;
    private ImageView imageView;

    /***************************************************
     * if you are use telecom device enter "Telecom_Device_Key" as your provided key otherwise send "" ;
     */
    private static final String Telecom_Device_Key = "";
    private byte[] t1,t2;

    //region USB intent and functions
    private UsbManager manager;
    private PendingIntent mPermissionIntent;
    private UsbDevice usb_Dev;
    private static final String ACTION_USB_PERMISSION = "my.academic.hpp.USB_PERMISSION";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                int pid, vid;
                pid = device.getProductId();
                vid = device.getVendorId();
                if ((pid == 0x8225 || pid == 0x8220)  && (vid == 0x0bca)) {
                    FM220SDK.stopCaptureFM220();
                    FM220SDK.unInitFM220();
                    usb_Dev=null;
                    textMessage.setText("FM220 disconnected");
                    DisableCapture();
                }
            }
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // call method to set up device communication
                            int pid, vid;
                            pid = device.getProductId();
                            vid = device.getVendorId();
                            if ((pid == 0x8225 || pid == 0x8220)  && (vid == 0x0bca)) {
                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                if (res.getResult()) {
                                    textMessage.setText("FM220 ready. "+res.getSerialNo());
                                    EnableCapture();
                                }
                                else {
                                    textMessage.setText("Error :-"+res.getError());
                                    DisableCapture();
                                }
                            }
                        }
                    } else {
                        textMessage.setText("User Blocked USB connection");
                        textMessage.setText("FM220 ready");
                        DisableCapture();
                    }
                }
            }
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        // call method to set up device communication
                        int pid, vid;
                        pid = device.getProductId();
                        vid = device.getVendorId();
                        if ((pid == 0x8225)  && (vid == 0x0bca) && !FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context,"Wrong device type application restart required!",Toast.LENGTH_LONG).show();
                            finish();
                        }
                        if ((pid == 0x8220)  && (vid == 0x0bca)&& FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context,"Wrong device type application restart required!",Toast.LENGTH_LONG).show();
                            finish();
                        }

                        if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                            if (!manager.hasPermission(device)) {
                                textMessage.setText("FM220 requesting permission");
                                manager.requestPermission(device, mPermissionIntent);
                            } else {
                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                if (res.getResult()) {
                                    textMessage.setText("FM220 ready. "+res.getSerialNo());
                                    EnableCapture();
                                }
                                else {
                                    textMessage.setText("Error :-"+res.getError());
                                    DisableCapture();
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        if (getIntent() != null) {
            return;
        }
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) && usb_Dev==null) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // call method to set up device communication & Check pid
                    int pid, vid;
                    pid = device.getProductId();
                    vid = device.getVendorId();
                    if ((pid == 0x8225)  && (vid == 0x0bca)) {
                        if (manager != null) {
                            if (!manager.hasPermission(device)) {
                                textMessage.setText("FM220 requesting permission");
                                manager.requestPermission(device, mPermissionIntent);
                            }
                            else {
                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                if (res.getResult()) {
                                    textMessage.setText("FM220 ready. "+res.getSerialNo());
                                    EnableCapture();
                                }
                                else {
                                    textMessage.setText("Error :-"+res.getError());
                                    DisableCapture();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mUsbReceiver);
            FM220SDK.unInitFM220();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    //endregion

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        //FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this);
        textMessage = (TextView) findViewById(R.id.textMessage);
        Capture_PreView = (Button) findViewById(R.id.authenticate);
        AuthCheck = (Button) findViewById(R.id.authenticate);
        imageView = (ImageView)  findViewById(R.id.authenticationFingerprintImage);

        //Region USB initialisation and Scanning for device
        SharedPreferences sp = getSharedPreferences("last_FM220_type", Activity.MODE_PRIVATE);
        boolean oldDevType = sp.getBoolean("FM220type", true);

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        final Intent piIntent = new Intent(ACTION_USB_PERMISSION);
        if (Build.VERSION.SDK_INT >= 16) piIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        mPermissionIntent = PendingIntent.getBroadcast(getBaseContext(), 1, piIntent, 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbReceiver, filter);

        UsbDevice device = null;

        for ( UsbDevice mdevice : manager.getDeviceList().values()) {
            int pid, vid;
            pid = mdevice.getProductId();
            vid = mdevice.getVendorId();
            boolean devType;
            if ((pid == 0x8225) && (vid == 0x0bca)) {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,true);
                devType=true;
            }
            else if ((pid == 0x8220) && (vid == 0x0bca)) {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,false);
                devType=false;
            } else {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
                devType=oldDevType;
            }
            if (oldDevType != devType) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("FM220type", devType);
                editor.apply();
            }
            if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                device  = mdevice;
                if (!manager.hasPermission(device)) {
                    textMessage.setText("FM220 requesting permission");
                    manager.requestPermission(device, mPermissionIntent);
                } else {
                    Intent intent = this.getIntent();
                    if (intent != null) {
                        if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                            finishAffinity();
                        }
                    }
                    fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                    if (res.getResult()) {
                        textMessage.setText("FM220 ready. "+res.getSerialNo());
                        EnableCapture();
                    }
                    else {
                        textMessage.setText("Error :-"+res.getError());
                        DisableCapture();
                    }
                }
                break;
            }
        }

        if (device == null) {
//            DisableCapture();
            textMessage.setText("Please connect FM220 Finger Print Reader ...");
            FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
        }

        Capture_PreView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DisableCapture();
                Toast.makeText(AuthenticationActivity.this, "Clicking the stuff", Toast.LENGTH_SHORT).show();
                FM220SDK.CaptureFM220(2,true,true);
            }
        });

        AuthCheck.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                /***
                 * if t1 and t2 is byte so you can use MatchFM220(byte[],baye[]) function
                 * and its String so you can use MatchFm220String(StringTmp1,StringTmp2) function
                 * for your Matching templets. and you want at time match your finger with scaning process use
                 * FM220SDK.MatchFM220(2, true, true, oldfingerprintisovale) function with pass old templet as perameter.
                 */

//                DisableCapture();
//                FM220SDK.MatchFM220(2, true, true, t1);

                if (t1 != null && t2 != null) {
                    if (FM220SDK.MatchFM220(t1, t2)) {
                        textMessage.setText("Finger matched");
                        t1 = null;
                        t2 = null;
                    } else {
                        textMessage.setText("Finger not matched");
                    }
                } else {
                    textMessage.setText("Pl capture first");
                }
//                String teamplet match example using FunctionBAse64 function .....
                FunctionBase64();
            }
        });
    }

    private void DisableCapture() {
        Capture_PreView.setEnabled(false);
        AuthCheck.setEnabled(false);
        imageView.setImageBitmap(null);
    }

    private void EnableCapture() {
        Capture_PreView.setEnabled(true);
        AuthCheck.setEnabled(true);
    }

    private void FunctionBase64() {
        try {
            String t1base64, t2base64;
            if (t1 != null && t2 != null) {
                t1base64 = Base64.encodeToString(t1, Base64.NO_WRAP);
                t2base64 = Base64.encodeToString(t2, Base64.NO_WRAP);
                if (FM220SDK.MatchFM220String(t1base64, t2base64)) {
                    Toast.makeText(getBaseContext(), "Finger matched", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Finger not matched", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ScannerProgressFM220(final boolean DisplayImage, final Bitmap ScanImage, final boolean DisplayText, final String statusMessage) {
        AuthenticationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DisplayText) {
                    textMessage.setText(statusMessage);
                    textMessage.invalidate();
                }
                if (DisplayImage) {
                    imageView.setImageBitmap(ScanImage);
                    imageView.invalidate();
                }
            }
        });
    }

    @Override
    public void ScanCompleteFM220(final fm220_Capture_Result result) {
        AuthenticationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (FM220SDK.FM220Initialized())  EnableCapture();
                if (result.getResult()) {
                    imageView.setImageBitmap(result.getScanImage());

//                  Method to upload
//                  to match the file from the server

                    Functionality.MatchFingerPrint(AuthenticationActivity.this,result.getScanImage(),true);

//                  byte [] isotem  = result.getISO_Template();

                    // ISO TEMPLET of FingerPrint ......
                    // isotem is byte value of fingerprints ......

//                    if (t1 == null) {
//                        t1 = result.getISO_Template();
//                    } else {
//                        t2 = result.getISO_Template();
//                    }

                    textMessage.setText("Success NFIQ:"+Integer.toString(result.getNFIQ())+"  SrNo:"+result.getSerialNo());

                } else {

                    imageView.setImageBitmap(null);
                    textMessage.setText(result.getError());

                }
                imageView.invalidate();
                textMessage.invalidate();
            }
        });
    }

    @Override
    public void ScanMatchFM220(final fm220_Capture_Result _result) {
        AuthenticationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (FM220SDK.FM220Initialized()) EnableCapture();
                if (_result.getResult()) {
                    imageView.setImageBitmap(_result.getScanImage());
                    textMessage.setText("Finger matched\n" + "Success NFIQ:" + Integer.toString(_result.getNFIQ()));
                } else {
                    imageView.setImageBitmap(null);
                    textMessage.setText("Finger not matched\n" + _result.getError());
                }
                imageView.invalidate();
                textMessage.invalidate();
            }
        });
    }
}