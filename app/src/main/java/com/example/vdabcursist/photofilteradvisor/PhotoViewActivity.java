package com.example.vdabcursist.photofilteradvisor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
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

        //private Camera camera;

        private Button vibrantView;
        private Button darkVibrantView;
        private Button lightVibrantView;
        private Button mutedView;
        private Button darkMutedView;

        private Bitmap bitmap;
        private TabLayout menu;
        private FloatingActionButton useCam;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        if (savedInstanceState != null) {
            bitmap = (Bitmap) savedInstanceState.getParcelable("BitmapImage");
        }

        paintTextBackground();

        //Use of camera

        menu = (TabLayout) findViewById(R.id.menu);
        menu.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

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

            useCam = (FloatingActionButton) findViewById(R.id.useCamera);
            //camera = new Camera(2, useCam);
            useCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCameraAccessAllowed()) {
                        Toast.makeText(v.getContext(), "You already have the permission to access Camera", Toast.LENGTH_LONG).show();
                        dispatchTakePictureIntent();
                        return;
                    }
                    requestCameraPermission();
                }

            });
        }

        //Save Instance

        @Override
        protected void onSaveInstanceState(Bundle savedInstanceState) {
            super.onSaveInstanceState(savedInstanceState);
            savedInstanceState.putParcelable("BitmapImage", bitmap);
        }

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
        bmOptions.inSampleSize = 4;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);

        paintTextBackground();

    }

    public void onClickResults(View v) {
        Intent intent = new Intent(this, ProductViewActivity.class);
        startActivity(intent);
    }

    //TabLayout

    private void onTabTapped(int position) {
        switch (position) {
            case 0:
                Toast.makeText(this, "First tab is tapped", Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(this, "Second tab is tapped", Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(this, "Third tab is tapped", Toast.LENGTH_LONG).show();
        }
    }

    //Palette elements

        private void paintTextBackground() {
        vibrantView = (Button) findViewById(R.id.vibrantView);
        darkVibrantView = (Button) findViewById(R.id.darkvibrantView);
        lightVibrantView = (Button) findViewById(R.id.lightvibrantView);
        mutedView = (Button) findViewById(R.id.mutedView);
        darkMutedView = (Button) findViewById(R.id.darkmutedView);

        mImageView = (ImageView) findViewById(R.id.profile);
        //camera.setmImageView((ImageView) findViewById(R.id.profile));

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);
        Bitmap bitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

            @Override
            public void onGenerated(Palette palette) {
                int defaultValue = 0x000000;
                int vibrant = palette.getVibrantColor(defaultValue);
                int darkVibrant = palette.getDarkVibrantColor(defaultValue);
                int lightVibrant = palette.getLightVibrantColor(defaultValue);
                int muted = palette.getMutedColor(defaultValue);
                int darkMuted = palette.getDarkMutedColor(defaultValue);

                    vibrantView.setBackgroundColor(vibrant);
                    darkVibrantView.setBackgroundColor(darkVibrant);
                    lightVibrantView.setBackgroundColor(lightVibrant);
                    mutedView.setBackgroundColor(muted);
                    darkMutedView.setBackgroundColor(darkMuted);

                    vibrantView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getRGBvalues(vibrantView);
                        }
                    });

                    darkVibrantView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getRGBvalues(darkVibrantView);
                        }
                    });

                    lightVibrantView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getRGBvalues(lightVibrantView);
                        }
                    });

                    mutedView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getRGBvalues(mutedView);
                        }
                    });

                    darkMutedView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getRGBvalues(darkMutedView);
                        }
                    });

                }
            });
        }

        private void getRGBvalues(Button colButton) {

            ColorDrawable buttonColor = (ColorDrawable) colButton.getBackground();
            int color = buttonColor.getColor();
            Toast.makeText(this, "Color: " + color, Toast.LENGTH_LONG).show();
        }

}
