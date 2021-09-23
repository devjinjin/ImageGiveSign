package kr.aimskorea.imagegivesign;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import kr.aimskorea.imagegivesign.view.SignMoveView;

public class SigningActivity extends AppCompatActivity {

    //이미지 뷰
    SignMoveView mSigningImageView;






    private final static float MIN_SCALE = 1.0f; // min and max for zoom level
    private final static float MAX_SCALE = 5.0f;

    private float mScaleFactor = 1.0f;


    private float originX = 0;
    private float originY = 0;



    private GestureDetector gesture;

    private ScaleGestureDetector mScaleGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing);
        //로드되어 보일 이미지뷰
        mSigningImageView = findViewById(R.id.cSign);
        //화면 세로 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    public static Activity getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //그림 로드
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.bike_docs);

        //이미지가 가져올수 있다면
        if (bitmap != null) {
            mSigningImageView.setBackgroundBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
        } else {
            //강제 종료
            finish();
        }

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        gesture = new GestureDetector(this, new GestureListener());

        mSigningImageView.setMoveViewClickListener(new SignMoveView.ISignMoveViewClickListener() {
            @Override
            public void onClickSignView(SignMoveView.SignType type) {
                if (type == SignMoveView.SignType.OFFICE_SIGN ){
                    Toast.makeText(getApplicationContext(), "관리자 사인", Toast.LENGTH_SHORT).show();
                }else if (type == SignMoveView.SignType.OFFICE_MANUAL ){
                    Toast.makeText(getApplicationContext(), "메뉴얼 확인", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "유저 사인 확인", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gesture.onTouchEvent(event)) {
            return mScaleGestureDetector.onTouchEvent(event);
        }
        return true;
    }



    private class GestureListener implements GestureDetector.OnGestureListener {


        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
            originX += dx / mScaleFactor; // we need to scale the movement delta for correct movement
            originY += dy / mScaleFactor;

            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private PointF viewportFocus = new PointF();
        private float lastSpanX;
        private float lastSpanY;


        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            // ScaleGestureDetector에서 factor를 받아 변수로 선언한 factor에 넣고
            mScaleFactor *= scaleGestureDetector.getScaleFactor();

            float fx = scaleGestureDetector.getFocusX();
            float fy = scaleGestureDetector.getFocusY();

            originX += fx / mScaleFactor; // move origin to focus
            originY += fy / mScaleFactor;


            mScaleFactor = Math.max(MIN_SCALE,
                    Math.min(mScaleFactor, MAX_SCALE));

            // 이미지뷰 스케일에 적용
            mSigningImageView.setScaleX(mScaleFactor);
            mSigningImageView.setScaleY(mScaleFactor);

            originX -= fx / mScaleFactor; // move back, allow us to zoom with (fx,fy) as center
            originY -= fy / mScaleFactor;

            return true;
        }
    }

}