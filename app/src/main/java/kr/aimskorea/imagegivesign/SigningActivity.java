package kr.aimskorea.imagegivesign;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.aimskorea.imagegivesign.models.UserInfo;
import kr.aimskorea.imagegivesign.view.InformationFragment;
import kr.aimskorea.imagegivesign.view.InputFragment;
import kr.aimskorea.imagegivesign.view.SignatureFragment;
import kr.aimskorea.imagegivesign.view.photoview.OnMatrixChangedListener;
import kr.aimskorea.imagegivesign.view.photoview.OnPhotoTapListener;
import kr.aimskorea.imagegivesign.view.photoview.OnSingleFlingListener;
import kr.aimskorea.imagegivesign.view.photoview.PhotoView;

public class SigningActivity extends AppCompatActivity
        implements SignatureFragment.IOnBitmapSaveListener,
        InputFragment.IOnInputTextResultListener, InformationFragment.IOnInformationResultListener {

    private PhotoView mPhotoView;

    private Toast mCurrentToast;
    private UserInfo userInfo = null;
    private boolean mIsUserChecked = false;

    private Button mConfirmButton;
    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_signing);

        mPhotoView = findViewById(R.id.iv_photo);



        userInfo = new UserInfo();
        userInfo.userName = "함정식";
        userInfo.managerName = "이진영";
        userInfo.modelColor = "블랙";
        userInfo.batteryId1 = "12345678912345678";
        userInfo.batteryId2 = "12345678912345678";
        userInfo.modelName = "레오";
        userInfo.modelNumber = "leo23123131231";
        userInfo.currentTime = new Date();


        mPhotoView.setUserInfo(userInfo);

        mConfirmButton = findViewById(R.id.btSignConfirm);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSignConfirmImage();
            }
        });
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.doc_image);

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                mPhotoView.setImageBitmap(((BitmapDrawable) drawable).getBitmap());
            }
        }


        // Lets attach some listeners, not required though!
        mPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
        mPhotoView.setOnPhotoTapListener(new PhotoTapListener());
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());
        checkConfirmButtonState();
    }

    public void checkConfirmButtonState() {
        if (userInfo != null && mPhotoView.isSigned() && mIsUserChecked) {
            mConfirmButton.setEnabled(true);
        } else {
            mConfirmButton.setEnabled(false);
        }

    }

    public void saveSignConfirmImage() {
        Bitmap bitmap = mPhotoView.getCurrentBitmap();

        addJpgSignatureToGallery(bitmap);

        Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_LONG).show();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
//            File photo = new File(getAlbumStorageDir("NanuManager"), String.format("NanuManager_%d.jpg", System.currentTimeMillis()));
            File photo = new File(getAlbumStorageDir("NanuManager"), String.format("NanuManager_%s.jpg", userInfo.modelNumber));
            saveBitmapToJpg(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        SigningActivity.this.sendBroadcast(mediaScanIntent);
    }

    public void saveBitmapToJpg(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }


    @Override
    public void onSaveSignPath(SignatureFragment.SignatureType pType, String pSignPathString) {
        File imgFile = new File(pSignPathString);

        if (imgFile.exists()) {

            Bitmap mUserSignBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.doc_image);

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    if (pType == SignatureFragment.SignatureType.USER) {
                        mPhotoView.setUserSignImage(((BitmapDrawable) drawable).getBitmap(), mUserSignBitmap);
                    } else {
                        mPhotoView.setOfficeSignImage(((BitmapDrawable) drawable).getBitmap(), mUserSignBitmap);
                    }
                    checkConfirmButtonState();
                }
            }

        }

//        Toast.makeText(getApplicationContext(), "사인정보 "+pSignPathString , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTextInputResult(String pInputText) {
        mPhotoView.setInputString(pInputText);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.doc_image);

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                mPhotoView.updateInfo(((BitmapDrawable) drawable).getBitmap());
                checkConfirmButtonState();
            }
        }
    }



    @Override
    public void onCheckInputResult(HashMap<Integer, Boolean> pUserCheckMap) {
        mIsUserChecked = true;
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.doc_image);
        mPhotoView.setIsUserCheckInformation(mIsUserChecked, pUserCheckMap);

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                mPhotoView.updateInfo(((BitmapDrawable) drawable).getBitmap());
                checkConfirmButtonState();
            }
        }
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
//            mCurrMatrixTv.setText(rect.toString());
        }
    }

    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            if ((6.0f < xPercentage) && (xPercentage < 91.4f) && (44.8f < yPercentage) && (yPercentage < 64.7f)) {
                InformationFragment e = InformationFragment.newInstance(SigningActivity.this);
                e.show(getSupportFragmentManager(), InformationFragment.class.getSimpleName());
            } else if ((8.3f < xPercentage) && (xPercentage < 50.0f) && (85.4f < yPercentage) && (yPercentage < 92.5f)) {
                SignatureFragment e = SignatureFragment.newInstance(SignatureFragment.SignatureType.OFFICE.ordinal());
                e.setOnBitmapSaveListener(SigningActivity.this);
                e.show(getSupportFragmentManager(), SignatureFragment.class.getSimpleName());

            } else if ((50.0f < xPercentage) && (xPercentage < 92.6f) && (85.4f < yPercentage) && (yPercentage < 92.5f)) {
                SignatureFragment e = SignatureFragment.newInstance(SignatureFragment.SignatureType.USER.ordinal());
                e.setOnBitmapSaveListener(SigningActivity.this);
                e.show(getSupportFragmentManager(), SignatureFragment.class.getSimpleName());
            } else if ((6.2f < xPercentage) && (xPercentage < 92.6f) && (70.0f < yPercentage) && (yPercentage < 74.2f)) {
                InputFragment e = InputFragment.newInstance(SigningActivity.this);
                e.show(getSupportFragmentManager(), InputFragment.class.getSimpleName());
            }
//            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }
    }

    private void showToast(CharSequence text) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(SigningActivity.this, text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    private class SingleFlingListener implements OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY));
            return true;
        }
    }

}