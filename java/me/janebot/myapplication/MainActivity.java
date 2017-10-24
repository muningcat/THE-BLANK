package me.janebot.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;

import me.janebot.myapplication.io.BitmapURIRepository;
import me.janebot.myapplication.io.DirectoryStorage;
import me.janebot.myapplication.io.FileImageReader;
import me.janebot.myapplication.io.FileImageWriter;
import me.janebot.myapplication.platformtools.core_application.ApplicationCore;
import me.janebot.myapplication.platformtools.notifications.NotificationListener;
import me.janebot.myapplication.platformtools.notifications.Parameters;

public class MainActivity extends AppCompatActivity implements NotificationListener {

    private final static String TAG = "MainActivity";
//FOR TRIALS CODES ONLY
    public static final int MY_PERMISSION_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";

    public static void saveToPreferences(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }
    public void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }
    //CHECK TUTORIALS POINT FOR THE CODES...
//END TRIALS CODE
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully!");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                }break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initializeButtons();

        ApplicationCore.initialize(this);
        //ProgressDialogHandler.initialize(this);
        //ProgressDialogHandler.getInstance().setDefaultProgressImplementor();

        //NotificationCenter.getInstance().addObserver(Notifications.ON_PROCESS_COMPLETED, this);

    }
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        DirectoryStorage.getSharedInstance().createDirectory();
        FileImageWriter.initialize(this);
        FileImageReader.initialize(this);
    }
    public void onDestroy() {
        //ProgressDialogHandler.destroy();
        FileImageWriter.destroy();
        FileImageReader.destroy();

        //NotificationCenter.getInstance().removeObserver(Notifications.ON_PROCESS_COMPLETED, this);
        super.onDestroy();
    }

    public void initializeButtons() {
        Button pickImgBtn = (Button) this.findViewById(R.id.selectBtn);
        pickImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startImagePickActivity();
            }
        });
        Button captureImgBtn = (Button) this.findViewById(R.id.openBtn);
        captureImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.openCamera();
            }
        });

    }

    public void startImagePickActivity() {
        Intent intent = new Intent(MainActivity.this, AlbumSelectActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 1);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            ArrayList<Uri> imageURIList = new ArrayList<>();
            for(int i = 0; i < images.size(); i++) {
                imageURIList.add(Uri.fromFile(new File(images.get(i).path)));
                BitmapURIRepository.getInstance().setImageURIList(imageURIList);
            }

            if(imageURIList.size() >= 1) {
                Log.v("LOG_TAG", "Selected Images " + imageURIList.size());
                this.startProcessing();

            }
            else {
                Toast.makeText(this, "You haven't picked enough image. At least 1.",
                        Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private void startProcessing() {
        //ProgressDialogHandler.getInstance().showProcessDialog("Processing", "Processing images");
        //ProcessingThread processingThread = new ProcessingThread();
        //processingThread.start();
    }
    @Override
    public void onNotify(String notificationString, Parameters params) {
        //if(notificationString == Notifications.ON_PROCESS_COMPLETED) {
            //ProgressDialogHandler.getInstance().hideProcessDialog();
            //Intent imageViewIntent = new Intent(MainActivity.this, ImageViewActivity.class);
            //this.startActivity(imageViewIntent);
        //}
    }
}
