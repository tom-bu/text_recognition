package com.tom.ml_demo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TextExtractionFragment extends Fragment {
    private View mView;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private OnFragmentInteractionListener mListener;

    public TextExtractionFragment() {
    }

    public static TextExtractionFragment newInstance() {
        TextExtractionFragment fragment = new TextExtractionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        mView = inflater.inflate(R.layout.fragment_text_extraction, container, false);
        mTextView = mView.findViewById(R.id.textView);
        mProgressBar = mView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //The loading screen for when the image is in the middle of processing
    public void loading(){
        mTextView = getView().findViewById(R.id.textView);
        mProgressBar = getView().findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setText("");
        mTextView.setGravity(Gravity.CENTER);
    }

    //the display in the scenario that no text is found in the image
    public void noText(){
        mTextView = getView().findViewById(R.id.textView);
        mProgressBar = getView().findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        mTextView.setText("No Text Found" + "\n" +"\n" + "Note: text must be in the same orientation as the camera");
        mTextView.setGravity(Gravity.CENTER);
    }

    //Display the text from the image
    public void displayExtraction (String string) {
        mTextView = getView().findViewById(R.id.textView);
        mProgressBar = getView().findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        mTextView.setText(string);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
