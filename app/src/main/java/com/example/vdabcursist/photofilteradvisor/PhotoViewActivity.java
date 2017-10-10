package com.example.vdabcursist.photofilteradvisor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.bitmap;

public class PhotoViewActivity extends AppCompatActivity {

        private int RUNTIME_PERMISSION_CODE = 2;
        static final int REQUEST_IMAGE_CAPTURE = 1;
        private ImageView mImageView;
        private String mCurrentPhotoPath;
        private TextView vibrantView;
        private TabLayout useCam;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        initViews();
        paintTextBackground();

        //Use of camera

        useCam = (TabLayout) findViewById(R.id.useCamera);
        useCam.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }
        });

            /*useCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCameraAccessAllowed()) {
                        Toast.makeText(v.getContext(), "You already have the permission to access Camera", Toast.LENGTH_LONG).show();
                        dispatchTakePictureIntent();
                        return;
                    }
                    requestCameraPermission();
                }

            });*/


            mImageView = (ImageView) findViewById(R.id.profile);
        }

        /*public void onClickCamera(View v) {
            if (isCameraAccessAllowed()) {
                Toast.makeText(v.getContext(), "You already have the permission to access Camera", Toast.LENGTH_LONG).show();
                dispatchTakePictureIntent();
                return;
            }
            requestCameraPermission();
        }*/

        //Asking Camera Permission

        private void requestCameraPermission() {

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

        //Asking External Storage permission

        private void requestExStoragePermission() {

        }

        private boolean isCameraAccessAllowed() {
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

        private void dispatchTakePictureIntent() {
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

        private File createImageFile() throws IOException {
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String imageFileName = "PIC_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }


    public void onClickResults(View v) {
        Intent intent = new Intent(this, ProductViewActivity.class);
        startActivity(intent);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    //TabLayout

    private void onTabTapped(int position) {
        switch (position) {
            case 0:
                Toast.makeText(this, "First tab is tapped", Toast.LENGTH_LONG).show();
                break;
            case 1:
                if (isCameraAccessAllowed()) {
                    Toast.makeText(this, "You already have the permission to access Camera", Toast.LENGTH_LONG).show();
                    dispatchTakePictureIntent();
                    return;
                }
                requestCameraPermission();
                break;
            case 2:
                Toast.makeText(this, "Third tab is tapped", Toast.LENGTH_LONG).show();
        }
    }

    //Palette elements

    private void initViews() {
        vibrantView = (TextView) findViewById(R.id.vibrantView);
    }

    private void paintTextBackground() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

            @Override
            public void onGenerated(Palette palette) {
                int defaultValue = 0x000000;
                int vibrant = palette.getVibrantColor(defaultValue);
                vibrantView.setBackgroundColor(vibrant);
            }
        });

    }

}
