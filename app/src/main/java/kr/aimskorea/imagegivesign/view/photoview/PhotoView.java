/*
 Copyright 2011, 2012 Chris Banes.
 <p>
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 <p>
 http://www.apache.org/licenses/LICENSE-2.0
 <p>
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package kr.aimskorea.imagegivesign.view.photoview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import kr.aimskorea.imagegivesign.models.UserInfo;

/**
 * A zoomable ImageView. See {@link PhotoViewAttacher} for most of the details on how the zooming
 * is accomplished
 */
@SuppressWarnings("unused")
public class PhotoView extends androidx.appcompat.widget.AppCompatImageView {

    private PhotoViewAttacher attacher;
    private ImageView.ScaleType pendingScaleType;


    private Bitmap mUserSignBitmap = null;
    private Bitmap mManagerSignBitmap = null;
    private UserInfo mUserInfo = null;
    private String mAdditionalString = "";
    private boolean mIsUserCheckInformation = false;
    private  HashMap<Integer, Boolean> mUserCheckMap = null;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init();
    }


    private void init() {
        attacher = new PhotoViewAttacher(this);
        //We always pose as a Matrix scale type, though we can change to another scale type
        //via the attacher
        super.setScaleType(ScaleType.MATRIX);
        //apply the previously applied scale type
        if (pendingScaleType != null) {
            setScaleType(pendingScaleType);
            pendingScaleType = null;
        }
    }

    public Bitmap getCurrentBitmap() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
        if (bitmapDrawable != null) {
            return bitmapDrawable.getBitmap();
        }
        return null;
    }

    /**
     * Get the current {@link PhotoViewAttacher} for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    public PhotoViewAttacher getAttacher() {
        return attacher;
    }

    @Override
    public ScaleType getScaleType() {
        return attacher.getScaleType();
    }

    @Override
    public Matrix getImageMatrix() {
        return attacher.getImageMatrix();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        attacher.setOnLongClickListener(l);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        attacher.setOnClickListener(l);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (attacher == null) {
            pendingScaleType = scaleType;
        } else {
            attacher.setScaleType(scaleType);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {

        super.setImageDrawable(drawable);


        // setImageBitmap calls through to this method
        if (attacher != null) {
            attacher.update();
        }
    }

    public void setInputString(String pInputText) {
        mAdditionalString = pInputText;
    }

    public void setIsUserCheckInformation(boolean pIsUserCheckInformation, HashMap<Integer, Boolean> pUserCheckMap) {
        mIsUserCheckInformation = pIsUserCheckInformation;
        mUserCheckMap = pUserCheckMap;
    }

    public void updateInfo(Bitmap bitmap) {
        Bitmap reloadBitmap = updateBitmap(bitmap);
        super.setImageBitmap(reloadBitmap);
    }

    private Bitmap updateBitmap(Bitmap bm) {
        Bitmap reloadBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);

        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important

        Paint paint = new Paint();
        paint.setAntiAlias(true);
//        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(20);
        int padding = 50;
        Canvas canvas = new Canvas(reloadBitmap);

        if (!mIsUserCheckInformation) {
            paint.setColor(Color.parseColor("#220000FF"));
            paint.setStrokeWidth(1f);
            Path path = new Path();
            path.moveTo(160, 1590);  //시작
            path.lineTo(2290, 1590); //오른쪽
            path.lineTo(2290, 2290); //아래
            path.lineTo(160, 2290); //왼쪽
            path.lineTo(160, 1590); //끝은 시작지점으로 돌아옵니다
            canvas.drawPath(path, paint);
        } else {
            paint.setColor(Color.BLACK);
            paint.setTextSize(60);
            if (mUserCheckMap!=null){
                for( Map.Entry<Integer, Boolean> elem : mUserCheckMap.entrySet() ){
                    System.out.println( String.format("키 : %s, 값 : %s", elem.getKey(), elem.getValue()) );
                    if ( elem.getKey() == 0){
                        //(1)
                        if (elem.getValue() == true){
                            canvas.drawText("V", 2020, 1780, paint);
                        }else{
                            canvas.drawText("V", 2180, 1780, paint);
                        }

                    }else if ( elem.getKey() == 1){
                        //(2)
                        if (elem.getValue() == true){
                            canvas.drawText("V", 2020, 1880, paint);
                        }else{
                            canvas.drawText("V", 2180, 1880, paint);
                        }
                    }else if ( elem.getKey() == 2){
                        //(3)
                        if (elem.getValue() == true){
                            canvas.drawText("V", 2020, 1975, paint);
                        }else{
                            canvas.drawText("V", 2180, 1975, paint);
                        }
                    }else if ( elem.getKey() == 3){

                        //(4)
                        if (elem.getValue() == true){
                            canvas.drawText("V", 2020, 2075, paint);
                        }else{
                            canvas.drawText("V", 2180, 2075, paint);
                        }
                    }else if ( elem.getKey() == 4){
                        //(5)
                        if (elem.getValue() == true){
                            canvas.drawText("V", 2020, 2175, paint);
                        }else{
                            canvas.drawText("V", 2180, 2175, paint);
                        }
                    }else if ( elem.getKey() == 5){
                        //(6)
                        if (elem.getValue() == true){
                            canvas.drawText("V", 2020, 2270, paint);
                        }else{
                            canvas.drawText("V", 2180, 2270, paint);
                        }
                    }

                }
            }



        }


        if (mUserInfo != null && mUserInfo.modelName != null && mUserInfo.modelName.length() > 0) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(60);

            canvas.drawText(mUserInfo.modelName, 320, 1370, paint);
        }

        if (mUserInfo != null && mUserInfo.modelColor != null && mUserInfo.modelColor.length() > 0) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(60);

            canvas.drawText(mUserInfo.modelColor, 740, 1370, paint);
        }

        if (mUserInfo != null && mUserInfo.modelNumber != null && mUserInfo.modelNumber.length() > 0) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(60);

            canvas.drawText(mUserInfo.modelNumber, 1060, 1370, paint);
        }

        if (mUserInfo != null && mUserInfo.batteryId1 != null && mUserInfo.batteryId1.length() > 0) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);

            canvas.drawText(mUserInfo.batteryId1, 1750, 1340, paint);
        }

        if (mUserInfo != null && mUserInfo.batteryId2 != null && mUserInfo.batteryId2.length() > 0) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);

            canvas.drawText(mUserInfo.batteryId2, 1750, 1390, paint);
        }

        if (mUserInfo != null && mUserInfo.managerName != null && mUserInfo.managerName.length() > 0) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(60);

            canvas.drawText(mUserInfo.managerName, 700, 3140, paint);
        }

        if (mUserInfo != null && mUserInfo.currentTime != null) {


            SimpleDateFormat full_sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a");
            Log.d("DATE", full_sdf.format(mUserInfo.currentTime).toString());

            SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
            String hour = hourFormat.format(mUserInfo.currentTime).toString();

            SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
            String minute = minuteFormat.format(mUserInfo.currentTime).toString();

            SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
            String day = dayFormat.format(mUserInfo.currentTime).toString();

            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            String month = monthFormat.format(mUserInfo.currentTime).toString();

            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            String year = yearFormat.format(mUserInfo.currentTime).toString();

            paint.setColor(Color.BLACK);
            paint.setTextSize(40);

            canvas.drawText(year, 900, 2925, paint);
            canvas.drawText(month, 1120, 2925, paint);
            canvas.drawText(day, 1300, 2925, paint);
            canvas.drawText(hour, 1480, 2925, paint);
            canvas.drawText(minute, 1650, 2925, paint);
        }


        if (mManagerSignBitmap != null) {
            canvas.drawBitmap(mManagerSignBitmap, 950, 3000, null);
        } else {
            Path path2 = new Path();
            paint.setColor(Color.parseColor("#2200FF00"));
            path2.moveTo(950, 3000);  //시작
            path2.lineTo(1250, 3000); //오른쪽
            path2.lineTo(1250, 3250); //아래
            path2.lineTo(950, 3250); //왼쪽
            path2.lineTo(950, 3000); //끝은 시작지점으로 돌아옵니다

            //이전 path와 역방향으로 그린다
//        path2.moveTo(250, 3050);
//        path2.lineTo(150, 2300);
//        path2.lineTo(2300, 2300);
//        path2.lineTo(2300, 1550);
//        path2.lineTo(150, 1550);
            canvas.drawPath(path2, paint);
        }

        if (mUserInfo != null && mUserInfo.userName != null && mUserInfo.userName.length() > 0) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(60);
            canvas.drawText(mUserInfo.userName, 1750, 3140, paint);
        }

        if (mUserSignBitmap != null) {
            canvas.drawBitmap(mUserSignBitmap, 2000, 3000, null);
        } else {
            Path path3 = new Path();
            paint.setColor(Color.parseColor("#22ff0000"));
            path3.moveTo(2000, 3000);  //시작
            path3.lineTo(2300, 3000); //오른쪽
            path3.lineTo(2300, 3250); //아래
            path3.lineTo(2000, 3250); //왼쪽
            path3.lineTo(2000, 3000); //끝은 시작지점으로 돌아옵니다
            canvas.drawPath(path3, paint);
        }

        if (mAdditionalString != null && mAdditionalString.length() > 0) {
            paint.setColor(Color.BLACK);
            paint.setTextSize(60);

            canvas.drawText(mAdditionalString, 200, 2550, paint);
        }

        return reloadBitmap;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        Bitmap reloadBitmap = updateBitmap(bm);
        super.setImageBitmap(reloadBitmap);
    }

    public void setUserInfo(UserInfo pUserInfo) {
        mUserInfo = pUserInfo;
    }

    public void setUserSignImage(Bitmap origin, Bitmap pSignBitmap) {

        mUserSignBitmap = pSignBitmap;
        Bitmap reloadBitmap = updateBitmap(origin);
        super.setImageBitmap(reloadBitmap);

        if (attacher != null) {
            attacher.update();
        }
    }

    public void setOfficeSignImage(Bitmap origin, Bitmap pSignBitmap) {

        mManagerSignBitmap = pSignBitmap;
        Bitmap reloadBitmap = updateBitmap(origin);
        super.setImageBitmap(reloadBitmap);

        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (attacher != null) {
            attacher.update();
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        if (changed) {
            attacher.update();
        }
        return changed;
    }

    public void setRotationTo(float rotationDegree) {
        attacher.setRotationTo(rotationDegree);
    }

    public void setRotationBy(float rotationDegree) {
        attacher.setRotationBy(rotationDegree);
    }

    public boolean isZoomable() {
        return attacher.isZoomable();
    }

    public void setZoomable(boolean zoomable) {
        attacher.setZoomable(zoomable);
    }

    public RectF getDisplayRect() {
        return attacher.getDisplayRect();
    }

    public void getDisplayMatrix(Matrix matrix) {
        attacher.getDisplayMatrix(matrix);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setDisplayMatrix(Matrix finalRectangle) {
        return attacher.setDisplayMatrix(finalRectangle);
    }

    public void getSuppMatrix(Matrix matrix) {
        attacher.getSuppMatrix(matrix);
    }

    public boolean setSuppMatrix(Matrix matrix) {
        return attacher.setDisplayMatrix(matrix);
    }

    public float getMinimumScale() {
        return attacher.getMinimumScale();
    }

    public float getMediumScale() {
        return attacher.getMediumScale();
    }

    public float getMaximumScale() {
        return attacher.getMaximumScale();
    }

    public float getScale() {
        return attacher.getScale();
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        attacher.setAllowParentInterceptOnEdge(allow);
    }

    public void setMinimumScale(float minimumScale) {
        attacher.setMinimumScale(minimumScale);
    }

    public void setMediumScale(float mediumScale) {
        attacher.setMediumScale(mediumScale);
    }

    public void setMaximumScale(float maximumScale) {
        attacher.setMaximumScale(maximumScale);
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        attacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        attacher.setOnMatrixChangeListener(listener);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        attacher.setOnPhotoTapListener(listener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener listener) {
        attacher.setOnOutsidePhotoTapListener(listener);
    }

    public void setOnViewTapListener(OnViewTapListener listener) {
        attacher.setOnViewTapListener(listener);
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        attacher.setOnViewDragListener(listener);
    }

    public void setScale(float scale) {
        attacher.setScale(scale);
    }

    public void setScale(float scale, boolean animate) {
        attacher.setScale(scale, animate);
    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        attacher.setScale(scale, focalX, focalY, animate);
    }

    public void setZoomTransitionDuration(int milliseconds) {
        attacher.setZoomTransitionDuration(milliseconds);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        attacher.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        attacher.setOnScaleChangeListener(onScaleChangedListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        attacher.setOnSingleFlingListener(onSingleFlingListener);
    }


    public boolean isSigned() {
        if (mUserSignBitmap != null && mManagerSignBitmap != null) {
            return true;
        }
        return false;
    }
}
