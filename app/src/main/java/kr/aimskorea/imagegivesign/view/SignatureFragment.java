package kr.aimskorea.imagegivesign.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import kr.aimskorea.imagegivesign.R;
import kr.aimskorea.imagegivesign.view.signature.views.SignaturePad;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignatureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignatureFragment extends DialogFragment implements View.OnClickListener {

    public enum SignatureType {
        USER,
        OFFICE
    }
    public void setOnBitmapSaveListener(IOnBitmapSaveListener pOnBitmapSaveListener) {
        this.mOnBitmapSaveListener = pOnBitmapSaveListener;
    }

    public interface IOnBitmapSaveListener{
        void onSaveSignPath(SignatureType pType, String pSignPathString);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "type";

    // TODO: Rename and change types of parameters
    private SignatureType mType = SignatureType.USER;
    SignaturePad mSignPad;
    Button mSignClearButton;
    Button mSaveButton;
    Button mSignBackButton;
    private IOnBitmapSaveListener mOnBitmapSaveListener;

    public SignatureFragment() {
        // Required empty public constructor
    }


    public static SignatureFragment newInstance(int pType) {
        SignatureFragment fragment = new SignatureFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, pType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            int typeInt = getArguments().getInt(ARG_TYPE);
            switch(typeInt) {
                case 0:
                    mType =  SignatureType.USER;
                    break;
                case 1:
                    mType =  SignatureType.OFFICE;
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_signature, container, false);
        
        ((TextView)v.findViewById(R.id.tvSignTitle)).setText(mType == SignatureType.USER ? "인수자 서명":"인도자 서명");

        mSignPad =  v.findViewById(R.id.cSignPad);
        mSignClearButton = v.findViewById(R.id.btSignClear);
        mSaveButton = v.findViewById(R.id.btSignSave);
        mSignBackButton = v.findViewById(R.id.btSignBack);


        mSignClearButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mSignBackButton.setOnClickListener(this);


        mSignPad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            //    Toast.makeText(getActivity(), "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSignClearButton.setEnabled(true);
                setCheckSaveButton();
            }

            @Override
            public void onClear() {
                mSignClearButton.setEnabled(false);
                setCheckSaveButton();
            }
        });
        return v;
    }

    private void setCheckSaveButton(){
        if (mSignClearButton.isEnabled()){
            mSaveButton.setEnabled(true);
        }else{
            mSaveButton.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.btSignSave:
                if ( mSignClearButton.isEnabled()){
                    if (mOnBitmapSaveListener != null){
                        onSaveSign();
                    }else{
                        dismiss();
                    }
                }else{
                    Toast.makeText(getContext(), "입력이 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btSignClear:
                mSignPad.clear();
                break;

            case R.id.btSignBack:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void onSaveSign() {
        Bitmap signBitmap = mSignPad.getSignatureBitmap();

        String signPathString = addPngSignatureToGallery(signBitmap);

        mOnBitmapSaveListener.onSaveSignPath(mType, signPathString);
        dismiss();

    }

    private void saveBitmapToPNG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
        stream.close();
    }

    private String addPngSignatureToGallery(Bitmap signature) {

        File photo = null;
        try {
            photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.png", System.currentTimeMillis()));
            saveBitmapToPNG(signature, photo);
            scanMediaFile(photo);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photo != null){
            return photo.getAbsolutePath();
        }else{
            return "";
        }
    }

    private File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }
}