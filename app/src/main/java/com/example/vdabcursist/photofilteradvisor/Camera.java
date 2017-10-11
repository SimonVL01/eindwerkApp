package com.example.vdabcursist.photofilteradvisor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by vdabcursist on 11/10/2017.
 */

public class Camera extends ImageCreator {

    private int RUNTIME_PERMISSION_CODE = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private FloatingActionButton useCam;

    public Camera(int RUNTIME_PERMISSION_CODE, FloatingActionButton useCam) {
        this.RUNTIME_PERMISSION_CODE = RUNTIME_PERMISSION_CODE;
        this.useCam = useCam;
    }

    public void requestCameraPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showAlertDialog();
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RUNTIME_PERMISSION_CODE);
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Runtime Permissions");

        alertDialogBuilder.setMessage("This is tutorial for Runtime Permission. " +
                "This needs permission of accessing your device Camera." +
                "Please grant the permission").setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public boolean isCameraAccessAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RUNTIME_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted now you can use the camera", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();
            setPic();
        }
    }

    public int getRUNTIME_PERMISSION_CODE() {
        return RUNTIME_PERMISSION_CODE;
    }

    public void setRUNTIME_PERMISSION_CODE(int RUNTIME_PERMISSION_CODE) {
        this.RUNTIME_PERMISSION_CODE = RUNTIME_PERMISSION_CODE;
    }

    public static int getRequestImageCapture() {
        return REQUEST_IMAGE_CAPTURE;
    }

    public FloatingActionButton getUseCam() {
        return useCam;
    }

    public void setUseCam(FloatingActionButton useCam) {
        this.useCam = useCam;
    }
}
