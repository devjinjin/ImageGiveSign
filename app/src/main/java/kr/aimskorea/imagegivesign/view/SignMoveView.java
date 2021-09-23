package kr.aimskorea.imagegivesign.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import kr.aimskorea.imagegivesign.R;
import kr.aimskorea.imagegivesign.SigningActivity;


public class SignMoveView extends FrameLayout {
    public enum SignType{
        OFFICE_SIGN,
        OFFICE_MANUAL,
        USER_SIGN
    }

    public interface ISignMoveViewClickListener{
        void onClickSignView(SignType type);
    }
    //출고증 이미지
    private Bitmap mBitmap;
    RelativeLayout mainFrame;
    private AppCompatImageView mImageView;
    private float initWidth = 0;
    private float initHeight = 0;
    private int mOrgWidth;
    private int mOrgHeight;
    private int bitmapWidth = 0;
    private int bitmapHeight = 0;
    private int viewWidth = 0;
    private int viewHeight = 0;
    private float scaleX = 1;
    private float scaleY = 1;

    private ISignMoveViewClickListener moveViewClickListener;

    public void setMoveViewClickListener(ISignMoveViewClickListener moveViewClickListener) {
        this.moveViewClickListener = moveViewClickListener;
    }
    public SignMoveView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SignMoveView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_sign_move, this, true);

        mainFrame = findViewById(R.id.rlMain);
        mImageView = findViewById(R.id.ivImage);
    }

    public void setBackgroundBitmap(Bitmap pBitmap, int pOrgWidth, int pOrgHeight) {

        mBitmap = pBitmap;
        mOrgWidth = pOrgWidth;
        mOrgHeight = pOrgHeight;

        mImageView.setImageBitmap(pBitmap);


        updateInnerView();
    }

    private void updateInnerView() {

        getLCDSize(SigningActivity.getActivity(this));
        resize();

        addButtons();

    }

    private void addButtons(){
        Button officeBtn = new Button(getContext());
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams((int) (initWidth/2 - (initWidth * 0.08)), (int) (initHeight * 0.05));
        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        officeBtn.setX((float) (initWidth * 0.08));
        officeBtn.setY((float) (initHeight * 0.09 * -1));
        officeBtn.setLayoutParams(params1);
        officeBtn.setBackgroundResource(R.drawable.sign_box);

        Button userBtn = new Button(getContext());
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams((int) (initWidth/2 - (initWidth * 0.08)), (int) (initHeight * 0.05));
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        userBtn.setX((float) (initWidth/2));
        userBtn.setY((float) (initHeight * 0.09 * -1));
        userBtn.setLayoutParams(params2);
        userBtn.setBackgroundResource(R.drawable.sign_box);


        Button contentBtn = new Button(getContext());
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams((int) (initWidth - (initWidth * 0.12)), (int) (initHeight * 0.22));
        params3.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        contentBtn.setX((float) (initWidth * 0.06));
        contentBtn.setY((float) (initWidth * 0.62));
        contentBtn.setLayoutParams(params3);
        contentBtn.setBackgroundResource(R.drawable.sign_box);


        mainFrame.addView(officeBtn);
        mainFrame.addView(userBtn);
        mainFrame.addView(contentBtn);
        officeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (moveViewClickListener!=null){
                   moveViewClickListener.onClickSignView(SignType.OFFICE_SIGN);
               }
            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moveViewClickListener!=null){
                    moveViewClickListener.onClickSignView(SignType.USER_SIGN);
                }
            }
        });
        contentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moveViewClickListener!=null){
                    moveViewClickListener.onClickSignView(SignType.OFFICE_MANUAL);
                }
            }
        });

    }

    private void resize() {
        bitmapWidth = mBitmap.getWidth();
        bitmapHeight = mBitmap.getHeight();

//        setSurfaceSize(bitmapWidth, bitmapHeight);
        ViewGroup.LayoutParams param = setSurfaceSize(bitmapWidth, bitmapHeight);
        initWidth = param.width;
        initHeight = param.height;

        scaleX = ((float) bitmapWidth / (float) param.width);
        scaleY = ((float) bitmapHeight / (float) param.height);
    }

    private ViewGroup.LayoutParams setSurfaceSize(int width, int height) {
//        private void setSurfaceSize(int width, int height) {
//        1920 1080
        float imageProportion = (float) width / (float) height;

        // Get the width of the screen
        int screenWidth = viewWidth;
        int screenHeight = viewHeight;
        float screenProportion = (float) screenWidth / (float) screenHeight;

        if (imageProportion > screenProportion) {
            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            param.width = screenWidth;
            param.height = (int) ((float) screenWidth / imageProportion);
            param.gravity = Gravity.CENTER_VERTICAL;
            this.setLayoutParams(param);
            return param;
        } else {
            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            param.width = (int) (imageProportion * (float) screenHeight);
            param.height = screenHeight;
            param.gravity = Gravity.CENTER_HORIZONTAL;
            this.setLayoutParams(param);
            return param;
        }
    }

    private int getStatusBarHeight() {
        int statusBarHeight = 0;

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }

    private void getLCDSize(Activity pContext) {

        //현재화면 크기
        Display display = pContext.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);


        // since SDK_INT = 1;
        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;


        try {
            Point realSize = new Point();
            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
            mWidthPixels = realSize.x;
            mHeightPixels = realSize.y;

        } catch (Exception e) {
            Log.e(SigningActivity.class.getSimpleName(), "getLCDSize() (Build.VERSION_CODES.JELLY_BEAN_MR1): " + e.getMessage());
        }

        viewWidth = mWidthPixels;

        int status = getStatusBarHeight();
        viewHeight = mHeightPixels - status;

    }


}
