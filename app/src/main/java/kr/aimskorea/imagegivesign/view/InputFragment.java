package kr.aimskorea.imagegivesign.view;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kr.aimskorea.imagegivesign.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputFragment extends DialogFragment {

    public void setOnInputTextListener(IOnInputTextResultListener pOnInputTextListener) {
        mOnInputTextListener = pOnInputTextListener;
    }

    public interface IOnInputTextResultListener{
        void onTextInputResult(String pInputText);
    }

    private IOnInputTextResultListener mOnInputTextListener;
    private EditText mInputText ;
    private Button mClearButton;
    private Button mSaveButton;

    public InputFragment() {
        // Required empty public constructor
    }
    public static InputFragment newInstance(IOnInputTextResultListener pOnBitmapSaveListener) {
        InputFragment fragment = new InputFragment();
        fragment.setOnInputTextListener(pOnBitmapSaveListener);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_input, container, false);
        mInputText = v.findViewById(R.id.etInputText);

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("jylee", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("jylee", "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("jylee", "afterTextChanged");
                if (editable.length() > 0){
                    if (editable.length() == 39){
                        Toast.makeText(getContext(), "입력이 초과되었습니다", Toast.LENGTH_SHORT).show();
                    }
                    mSaveButton.setEnabled(true);

                }else{
                    mSaveButton.setEnabled(false);
                }
            }
        });
        v.findViewById(R.id.btInputBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mClearButton = v.findViewById(R.id.btInputClear);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputText.setText("");
            }
        });

        mSaveButton  =  v.findViewById(R.id.btInputSave);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnInputTextListener!=null){
                    if (mInputText.length() > 0){
                        mOnInputTextListener.onTextInputResult(mInputText.getText().toString());
                    }else{
                        mOnInputTextListener.onTextInputResult("");
                    }
                    dismiss();
                }
            }
        });
        return v;
    }
}