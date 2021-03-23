package com.smg.thewardrobe;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.smg.thewardrobe.adapters.BottomWearPagerAdapter;
import com.smg.thewardrobe.adapters.TopWearPagerAdapter;
import com.smg.thewardrobe.databsehelpers.DatabaseManipulator;
import com.smg.thewardrobe.models.FavoriteWearModel;
import com.smg.thewardrobe.models.ImageWearModel;
import com.smg.thewardrobe.utilities.GetRandomImageUtility;
import com.smg.thewardrobe.utilities.ImageUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static String TOPWEAR = "TOP";
    public static String BOTTOMWEAR = "BOTTOM";
    ViewPager vpTopWearPager, vpBottomWearPager;
    ImageView ivFavorite, ivAddTopWear, ivAddBottomWear, ivReshuffle;

    int reshuffleCount = 0;

    //ImageView ivTopWear, ivBottomWear;

    ArrayList<ImageWearModel> arrTopWears, arrBottomWears;
    ArrayList<FavoriteWearModel> arrFavWears;
    TopWearPagerAdapter topPagerAdapter;
    BottomWearPagerAdapter bottomPagerAdapter;

    private Bitmap bitmap;
    private File imageDestination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private final int PICK_TOPWEAR_CAMERA_CODE = 101, PICK_TOPWEAR_GALLERY_CODE = 102;
    private final int PICK_BOTTOMWEAR_CAMERA_CODE = 103, PICK_BOTTOMWEAR_GALLERY_CODE = 104;

    DatabaseManipulator dbManipulator;
    int PERMISSION_CODE = 1;
    String[] PERMISSIONS_REQUIRED = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check for permissions and ask user if not given already.
        if (!checkRequiredPermissions(this, PERMISSIONS_REQUIRED)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSION_CODE);
        }


        dbManipulator = new DatabaseManipulator(this);
        dbManipulator.open();

        vpTopWearPager = findViewById(R.id.viewpager_topwear);
        vpBottomWearPager = findViewById(R.id.viewpager_bottomwear);

        ivFavorite = findViewById(R.id.iv_favourite);
        ivAddTopWear = findViewById(R.id.iv_add_topwear);
        ivAddBottomWear = findViewById(R.id.iv_add_bottomwear);
        ivReshuffle = findViewById(R.id.iv_shuffle);

        arrTopWears = dbManipulator.getAllTopWears();
        topPagerAdapter = new TopWearPagerAdapter(getApplicationContext(), arrTopWears);
        vpTopWearPager.setAdapter(topPagerAdapter);
        vpTopWearPager.setCurrentItem(0);

        arrBottomWears = dbManipulator.getAllBottomWears();
        bottomPagerAdapter = new BottomWearPagerAdapter(getApplicationContext(), arrBottomWears);
        vpBottomWearPager.setAdapter(bottomPagerAdapter);
        vpBottomWearPager.setCurrentItem(0);

        arrFavWears = dbManipulator.getAllFavoriteWears();

        vpTopWearPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageWearModel topWearModel = arrTopWears.get(position);
                ImageWearModel bottomWearModel = arrBottomWears.get(vpBottomWearPager.getCurrentItem());

                //If -1, such pair in favorite doesn't exist
                if(getIDFromFavorites(topWearModel.id, bottomWearModel.id) >= 0){
                    ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_filled_18));
                }
                else {
                    ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_18));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        vpBottomWearPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageWearModel bottomWearModel = arrBottomWears.get(position);
                ImageWearModel topWearModel = arrTopWears.get(vpTopWearPager.getCurrentItem());

                //If -1, such pair in favorite doesn't exist
                if(getIDFromFavorites(topWearModel.id, bottomWearModel.id) >= 0){
                    ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_filled_18));
                }
                else {
                    ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_18));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void toggleFavorite(View v){

        if(arrTopWears.size() > 0 && arrBottomWears.size() > 0) {
            //Check if icon state is not yet favorite
            if (ivFavorite.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_favorite_border_18)
                    .getConstantState()) {
                //Impact on UI
                ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_filled_18));

                //Changes in database

                long top_id = arrTopWears.get(vpTopWearPager.getCurrentItem()).id;
                long bottom_id = arrBottomWears.get(vpBottomWearPager.getCurrentItem()).id;

                long id = dbManipulator.insertFavoriteWear(top_id, bottom_id);

                arrFavWears.add(0, new FavoriteWearModel(id, top_id, bottom_id));

            } else if (ivFavorite.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_favorite_filled_18)
                    .getConstantState()) {
                //Impact on UI
                ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_18));

                //Changes in database

                long top_id = arrTopWears.get(vpTopWearPager.getCurrentItem()).id;
                long bottom_id = arrBottomWears.get(vpBottomWearPager.getCurrentItem()).id;

                long id = getIDFromFavorites(top_id, bottom_id);

                dbManipulator.removeFromFavorites(id);

                arrFavWears.remove(id);
            }
        }
    }

    public void addTopWear(View v){
        selectImage(TOPWEAR);
    }

    public void addBottomWear(View v){
        selectImage(BOTTOMWEAR);

    }

    public void shuffleWears(View v){
        if(arrTopWears.size() > 0 && arrBottomWears.size() > 0) {
            reshuffleCount++;
            int topWearID = 0, bottomWearID = 0;
            if (reshuffleCount <= 3 || arrFavWears.size() == 0) {
                //Select random top wear and bottom wear irrespective of its favorite or not.

                topWearID = GetRandomImageUtility.getRandomImageFromArray(arrTopWears);
                bottomWearID = GetRandomImageUtility.getRandomImageFromArray(arrBottomWears);

            } else {
                //Select a favorite wear if available and reset the counter
                int favoriteWearID = GetRandomImageUtility.getRandomImageFromArray(arrFavWears);

                topWearID = getPositionForTopID(arrFavWears.get(favoriteWearID).top_id);
                bottomWearID = getPositionForBottomID(arrFavWears.get(favoriteWearID).bottom_id);

                reshuffleCount = 0;
            }

            vpTopWearPager.setCurrentItem(topWearID);
            vpBottomWearPager.setCurrentItem(bottomWearID);
        }
    }

    // Select image from camera and gallery
    private void selectImage(final String WEAR_TYPE) {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select Option");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();

                            if(WEAR_TYPE.equals(TOPWEAR)) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, PICK_TOPWEAR_CAMERA_CODE);
                            }
                            if(WEAR_TYPE.equals(BOTTOMWEAR)) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, PICK_BOTTOMWEAR_CAMERA_CODE);
                            }

                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            if(WEAR_TYPE.equals(TOPWEAR)) {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, PICK_TOPWEAR_GALLERY_CODE);
                            }
                            else if(WEAR_TYPE.equals(BOTTOMWEAR)) {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, PICK_BOTTOMWEAR_GALLERY_CODE);
                            }

                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }

                    }
                });

                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_TOPWEAR_CAMERA_CODE) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                long img_id = dbManipulator.insertTopWear(bitmap);

                arrTopWears.add(0, new ImageWearModel(img_id,  ImageUtility.getBytes(bitmap)));
                topPagerAdapter.notifyDataSetChanged();
                vpTopWearPager.invalidate();

                vpTopWearPager.setCurrentItem(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_TOPWEAR_GALLERY_CODE) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", "Pick from Gallery::>>> ");

                long img_id = dbManipulator.insertTopWear(bitmap);

                arrTopWears.add(0, new ImageWearModel(img_id,  ImageUtility.getBytes(bitmap)));
                topPagerAdapter.notifyDataSetChanged();
                vpTopWearPager.invalidate();

                vpTopWearPager.setCurrentItem(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == PICK_BOTTOMWEAR_CAMERA_CODE) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                long img_id = dbManipulator.insertBottomWear(bitmap);

                arrBottomWears.add(0, new ImageWearModel(img_id,  ImageUtility.getBytes(bitmap)));
                bottomPagerAdapter.notifyDataSetChanged();
                vpBottomWearPager.invalidate();

                vpBottomWearPager.setCurrentItem(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_BOTTOMWEAR_GALLERY_CODE) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", "Pick from Gallery::>>> ");

                long img_id = dbManipulator.insertBottomWear(bitmap);

                arrBottomWears.add(0, new ImageWearModel(img_id,  ImageUtility.getBytes(bitmap)));
                bottomPagerAdapter.notifyDataSetChanged();
                vpBottomWearPager.invalidate();

                vpBottomWearPager.setCurrentItem(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManipulator.close();
    }

    public static boolean checkRequiredPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public long getIDFromFavorites(long top_id, long bottom_id){
        for (int i = 0; i < arrFavWears.size(); i++)
        {
            FavoriteWearModel favModel = arrFavWears.get(i);

            if(favModel.top_id == top_id && favModel.bottom_id == bottom_id){
                return favModel.id;
            }

        }

        return -1;
    }

    public int getPositionForTopID(long top_id){
        for (int i = 0; i < arrTopWears.size(); i++){
            if(arrTopWears.get(i).id == top_id)
                return i;

        }
        return 0;
    }

    public int getPositionForBottomID(long bottom_id){
        for (int i = 0; i < arrBottomWears.size(); i++){
            if(arrBottomWears.get(i).id == bottom_id)
                return i;

        }
        return 0;
    }
}