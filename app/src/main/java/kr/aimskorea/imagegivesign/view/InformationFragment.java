package kr.aimskorea.imagegivesign.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

import kr.aimskorea.imagegivesign.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InformationFragment extends DialogFragment {
    HashMap<Integer, Boolean> mUserCheckMap = new HashMap<Integer, Boolean>() {{
        put(0, false);
        put(1, false);
        put(2, false);
        put(3, false);
        put(4, false);
        put(5, false);
    }};

    private RadioGroup mFirst;
    private RadioGroup mSecond;
    private RadioGroup mThird;
    private RadioGroup mFourth;
    private RadioGroup mFifth;
    private RadioGroup mSixth;

    public void setOnInformationResultListener(IOnInformationResultListener pOnInformationResultListener) {
        mOnInformationResultListener = pOnInformationResultListener;
    }

    public interface IOnInformationResultListener {
        void onCheckInputResult(HashMap<Integer, Boolean> pUserCheckMap);
    }

    private IOnInformationResultListener mOnInformationResultListener;

    public InformationFragment() {
        // Required empty public constructor
    }

    public static InformationFragment newInstance(IOnInformationResultListener pOnInformationResultListener) {
        InformationFragment fragment = new InformationFragment();
        fragment.setOnInformationResultListener(pOnInformationResultListener);
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
        View v = inflater.inflate(R.layout.fragment_information, container, false);
        v.findViewById(R.id.btInformationBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        v.findViewById(R.id.btInformationSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnInformationResultListener != null) {

                    mFirst = v.findViewById(R.id.rgFirst);
                    mSecond =v.findViewById(R.id.rgSecond);
                    mThird =v.findViewById(R.id.rgThird);
                    mFourth = v.findViewById(R.id.rgFourth);
                    mFifth =v.findViewById(R.id.rgFifth);
                    mSixth =v.findViewById(R.id.rgSixth);

                    if (mFirst.getCheckedRadioButtonId() == R.id.rbFirstYes) {
                        mUserCheckMap.put(0, true);
                    }else{
                        mUserCheckMap.put(0, false);
                    }
                    if (mSecond.getCheckedRadioButtonId() == R.id.rbSecondYes) {
                        mUserCheckMap.put(1, true);
                    }else{
                        mUserCheckMap.put(1, false);
                    }
                    if (mThird.getCheckedRadioButtonId() == R.id.rbThirdYes) {
                        mUserCheckMap.put(2, true);
                    }else{
                        mUserCheckMap.put(2, false);
                    }
                    if (mFourth.getCheckedRadioButtonId() == R.id.rbFourthYes) {
                        mUserCheckMap.put(3, true);
                    }else{
                        mUserCheckMap.put(3, false);
                    }
                    if (mFifth.getCheckedRadioButtonId() == R.id.rbFifthYes) {
                        mUserCheckMap.put(4, true);
                    }else{
                        mUserCheckMap.put(4, false);
                    }
                    if (mSixth.getCheckedRadioButtonId() == R.id.rbSixthYes) {
                        mUserCheckMap.put(5, true);
                    }else{
                        mUserCheckMap.put(5, false);
                    }
                    mOnInformationResultListener.onCheckInputResult(mUserCheckMap);
                }
                dismiss();
            }
        });

        mFirst = v.findViewById(R.id.rgFirst);
        mSecond =v.findViewById(R.id.rgSecond);
        mThird =v.findViewById(R.id.rgThird);
        mFourth = v.findViewById(R.id.rgFourth);
        mFifth =v.findViewById(R.id.rgFifth);
        mSixth =v.findViewById(R.id.rgSixth);

        return v;
    }
}