package com.tom.ml_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    private Camera mCamera;
    private FrameLayout mFrameLayout;
    private CameraPreview mCameraPreview;
    private Button mCaptureButton;
    private StringBuilder mStringBuilder = new StringBuilder();
    private OnFragmentInteractionListener mListener;

    public CameraFragment() {
    }

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
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
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mFrameLayout = view.findViewById(R.id.frameLayout);
        mCaptureButton = view.findViewById(R.id.button);
        initializeCamera();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
        // release the camera immediately on pause event
        releaseCamera();
        mCamera = null;
        mFrameLayout.removeView(mCameraPreview);
        mCameraPreview = null;
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
    public void onResume() {
        super.onResume();
        // Get the Camera instance as the activity achieves full user focus
        if (mCamera == null) {
            initializeCamera();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //Activate the loading screen while the image is being processed
            ((TextExtractionFragment)getFragmentManager().findFragmentById(R.id.pager)).loading();
            //Resume the camera preview
            mCamera.startPreview();
            //Feed the image into the ML model
            Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);

            //forces the picture to be vertical to align with the portrait mode
            if (bitmap.getWidth() > bitmap.getHeight()){
                bitmap = fixOrientation(bitmap);
            }

            runTextRecognition(bitmap);
        }
    };

    protected void initializeCamera(){
        //open the camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mCameraPreview = new CameraPreview(getActivity(), getActivity(), mCamera);
        mFrameLayout.addView(mCameraPreview);

        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an image from the camera and switch to the next screen
                mCamera.takePicture(null, null, mPicture);
                ((MainActivity)getActivity()).setCurrentItem (1, true);

            }
        }
            );
    }


    private void runTextRecognition(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                .getVisionTextDetector();
        detector.detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.Block> blocks = texts.getBlocks();
        if (blocks.size() == 0) {
            ((TextExtractionFragment)getFragmentManager().findFragmentById(R.id.pager)).noText();
            return;
        }
        for (int i = 0; i < blocks.size(); i++) {
            mStringBuilder.append(blocks.get(i).getText());
            mStringBuilder.append("\n" + "\n");
        }
        //display all recognized text onto the next screen
        ((TextExtractionFragment)getFragmentManager().findFragmentById(R.id.pager)).displayExtraction(mStringBuilder.toString());
        mStringBuilder.setLength(0);

    }

    //rotates the bitmap so that it will return vertical
    public Bitmap fixOrientation(Bitmap mBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        mBitmap = Bitmap.createBitmap(mBitmap , 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        return mBitmap;
    }
}
