package com.example.ascope_lite;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.canhub.cropper.CropImageView;
import com.example.ascope_lite.CamTest_v01.calculation.AutoFitTextureView;
import com.example.ascope_lite.CamTest_v01.calculation.CompareSizesByArea;
import com.example.ascope_lite.CamTest_v01.calculation.ImageSaver;
import com.example.ascope_lite.CrackInspect.CrackData;
import com.example.ascope_lite.CrackInspect.CrackResponse;
import com.example.ascope_lite.Planlist.PlanListImgActivity;
import com.example.ascope_lite.Planlist.PlanResponse;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraFragment extends Fragment {
    ///////////////////////////////////////
    Loading_Progress loading_progress;
    ApiService service;
    AlertDialog alertDialog;
    Dialog dialog;
    Button inputBtn, cancel_Btn, planListBtn, capture, crop, cancelCrop;
    CheckBox size_ch, length_ch, planData_ch;
    EditText crackSize_edt, crackLength_edt, crack_etcEdt;
    Spinner crack_position_sp;
    CheckBox ch_timer;
    EditText edt_timer;

    String plan_id = null;

    private float x;
    private float y;

    //activity 종료 시 결과 값 받는 ActivityResultLauncher (startActivityForResult deprecated 대체)
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    x = intent.getFloatExtra("x", 0);
                    y = intent.getFloatExtra("y", 0);
                    if (x == 0 && y == 0) {
                        inputBtn.setEnabled(false);
                        inputBtn.setBackground(getResources().getDrawable(R.drawable.button_cancle));
                    } else {
                        inputBtn.setEnabled(true);
                        inputBtn.setBackground(getResources().getDrawable(R.drawable.button_ok));
                        ;
                        planData_ch.setText("입력 완료");
                        planData_ch.setChecked(true);
                    }
//                    Log.e("ActivityResultLauncher", " " + intent.getFloatExtra("x", 0));
//                    Log.e("ActivityResultLauncher", " " + intent.getFloatExtra("y", 0));
                }
            }
    );
    ///////////////////////////////////////
    private static String TAG = "CameraFragment";

    //region Camera Configruation
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;
    private int mState = STATE_PREVIEW;
    private int focus_mode = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE;

    private CameraCharacteristics characteristics;
    private String mCameraId;
    private AutoFitTextureView mTextureView;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private Size mPreviewSize;

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private ImageReader mImageReader;
    private File mFile;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;

    private final Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private boolean mFlashSupported;
    private int mSensorOrientation;
    //endregion Camera Configuration
    private Bitmap imageBeforeCrop = null;
    private CropImageView cropImageView;
    private RadioGroup radioGroup;
    private SeekBar focusSeek;
    private float lensFocusDistance;
    private String realResult;

    boolean timer_checked = false;

    int time_value = 0;

//    BackgroundTask task;

    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_camera_camtest, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
        int inspect_URL = preferences.getInt("inspect_URL", 0);
        int URL_flag = preferences.getInt("URL_flag", 0);
        String Token = "Bearer " + preferences.getString("token", "null");

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);


        loading_progress = new Loading_Progress(getActivity());
        loading_progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_progress.setCancelable(false);

        //timer
        ch_timer = view.findViewById(R.id.ch_timer);
        edt_timer = view.findViewById(R.id.edt_timer);

        ch_timer.setOnClickListener(v -> {
            if (ch_timer.isChecked()) {
                timer_checked = true;
                edt_timer.setVisibility(View.VISIBLE);
            } else {
                timer_checked = false;
                edt_timer.setVisibility(View.GONE);
            }
        });

        cropImageView = view.findViewById(R.id.cropImageView);
        //cropImageView.setAspectRatio(1,1);
        mTextureView = view.findViewById(R.id.cameraPreview);
        radioGroup = view.findViewById(R.id.radioGroup);
        focusSeek = view.findViewById(R.id.seek);
        radioGroup.check(R.id.radio_af);
        capture = view.findViewById(R.id.capture);
        crop = view.findViewById(R.id.crop);
        crop.setText("정보 등록");
        cancelCrop = view.findViewById(R.id.cancelCrop);

        Bundle bundle = getArguments();
        CrackData requestData = (CrackData) bundle.getSerializable("send_bundle");

        Log.e("ProjectId: ", requestData.getProjectid());

        capture.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (timer_checked) {
                    if (edt_timer.getText().toString().length() == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "타이머 시간을 입력하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        time_value = Integer.parseInt(edt_timer.getText().toString());
                    }
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            takePicture();
                        }
                    }, time_value * 1000L);
                } else {
                    takePicture();
                }
            }
        });
        crop.setOnClickListener(v -> {
            getCropImage();
        });

        cancelCrop.setOnClickListener(v -> {
            cropImageView.setImageBitmap(null);
            unlockFocus();
        });

        CheckBox cb23 = (CheckBox) view.findViewById(R.id.cBox23);
        cb23.setVisibility(View.GONE);
        CheckBox cb24 = (CheckBox) view.findViewById(R.id.cBox24);
        cb24.setVisibility(View.GONE);

        setBackKeyEvent(view);

        edt_timer.setOnKeyListener((view1, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//                Log.e("edt_timer", "capture.performClick()");
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                edt_timer.clearFocus();
                capture.performClick();
                return true;
            } else {
                return false;
            }

        });
    }

    private void setBackKeyEvent(View view) {
//        Log.e("setBackKeyEvent", "start");
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        view.setOnKeyListener((view1, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//                Log.e("setBackKeyEvent", "capture.performClick()");
                capture.performClick();
                focusSeek.setVisibility(View.GONE);
                return true;
            } else {
                return false;
            }

        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFile = new File(getActivity().getExternalFilesDir(null), "pic.jpg");

    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    //region Camera Initialize
    private int getOrientation(int rotation) {
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), sessionStateListener, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            if (focus_mode != CaptureRequest.CONTROL_AF_MODE_OFF) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            }
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void unlockFocus() {
        try {
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);

            // Reset the auto-focus trigger
            if (focus_mode != CaptureRequest.CONTROL_AF_MODE_OFF) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            }
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    //endregion Camera Initialize

    public void takePicture() {
        if (STATE_PREVIEW == mState) {
            lockFocus();
        }
    }

    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            if (focus_mode != CaptureRequest.CONTROL_AF_MODE_OFF) {
                captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            }

            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startCropImage(Bitmap bitmap) {
        int rotation = requireActivity().getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        matrix.postRotate(getOrientation(rotation));

        cropImageView.post(() -> {
            cropImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
            imageBeforeCrop = bitmap;
        });
    }

    private void getCropImage() {
        Bitmap croppedImage = cropImageView.getCroppedImage();
        if (croppedImage != null && imageBeforeCrop != null) {

//                Log.e("testtest", "getCropImage: w:" + croppedImage.getWidth() + " / h:" + croppedImage.getHeight());
//                Log.e("testtest", "original: w:" + imageBeforeCrop.getWidth() + " / h:" + imageBeforeCrop.getHeight());
            float[] original = new float[]{imageBeforeCrop.getWidth(), imageBeforeCrop.getHeight()};
            float[] cropped = new float[]{croppedImage.getWidth(), croppedImage.getHeight()};
            float[] result = getRealSize(original, cropped);

            unlockFocus();
            File f = createFileFromBitmap(croppedImage);
            String f_path = f.getAbsolutePath();
            Bundle bundle = getArguments();
            int menu = bundle.getInt("menu_f");
            int type = bundle.getInt("type", 0);
            Log.e("type", "" + type);

            if (menu == 1) {//crack (수동)
                showCrackDialog(f_path, type);
            } else {
                //수정에서 넘어온 경우
                Log.e("getCropImage()", "menu = " + menu);
                Log.e("getCropImage()", "f_path = " + f_path);

                Intent result_intent = new Intent();
                result_intent.putExtra("new_img", f_path);
                getActivity().setResult(9001, result_intent);
                getActivity().finish();

            }

            // 촬영 데이터 초기화
            cropImageView.setImageBitmap(null);
            imageBeforeCrop = null;

        } else {
            showToast("촬영을 먼저 해주세요.");
        }
    }

    private File createFileFromBitmap(Bitmap b) {
        @SuppressLint("SimpleDateFormat") String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName = getUUID(requireContext()) + "_" + fileName + ".jpg";
        File f = new File(requireContext().getCacheDir(), fileName);

        try {
            //create a file to write bitmap data
            f.createNewFile();
            Log.e(TAG + " 비트맵 내부 저장소 저장:", fileName);
            //Convert bitmap to byte array
            Matrix matrix = new Matrix();
            matrix.setScale(0.25f, 0.25f);

            Bitmap bitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
            b.recycle();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    private float[] getRealSize(float[] original, float[] cropped) {
        float focalLength = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0] * 10; // mm to cm
        SizeF sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        float sensorHeight = Math.max(sensorSize.getWidth(), sensorSize.getHeight()) * 10; // mm to cm
        float sensorWidth = Math.min(sensorSize.getWidth(), sensorSize.getHeight()) * 10; // mm to cm
        float cropHeight = Math.max(cropped[0], cropped[1]);
        float cropWidth = Math.min(cropped[0], cropped[1]);
        float originalHeight = Math.max(original[0], original[1]);
        float originalWidth = Math.min(original[0], original[1]);

        float frameWidth = lensFocusDistance * sensorWidth / focalLength;
        float frameHeight = lensFocusDistance * sensorHeight / focalLength;
        float realWidth = frameWidth / originalWidth * cropWidth;
        float realHeight = frameHeight / originalHeight * cropHeight;
        realResult = "d=" + lensFocusDistance + "cm\nwidth=" + realWidth + "cm" + "\nheight=" + realHeight + "cm";
        Log.d(TAG, "getRealSizePerPixel: " + realResult);
        return new float[]{realWidth, realHeight};
    }

    private static String getUUID(Context _context) {
        UUID uuid = null;
        String androidId = Settings.Secure.getString(_context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null || androidId.isEmpty() || androidId.equals("9774d56d682e549c")) {
            uuid = UUID.randomUUID();
        } else {
            try {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("UTF8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                uuid = UUID.randomUUID();
            }
        }
        return uuid.toString();
    }

    //region Event listener
    private final CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            lensFocusDistance = 1f / result.get(CaptureResult.LENS_FOCUS_DISTANCE) * 100; // m to cm
            Log.d(TAG, mFile.toString());
            Log.d(TAG, "getCropImage2: " + CaptureResult.LENS_FOCUS_DISTANCE);

        }
    };

    final CameraCaptureSession.StateCallback sessionStateListener = new CameraCaptureSession.StateCallback() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            // The camera is already closed
            if (null == mCameraDevice) {
                return;
            }

            // When the session is ready, we start displaying the preview.
            mCaptureSession = cameraCaptureSession;
            try {
                final float minFocus = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
                focusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mPreviewRequestBuilder.set(
                                CaptureRequest.LENS_FOCUS_DISTANCE,
                                minFocus * (progress * 0.01f)  /*0.0f means infinity focus  10f는 가까운 초점  0f에 가까울 수록 먼 곳에 초점을 잡는다.*/
                        );
                        mPreviewRequest = mPreviewRequestBuilder.build();
                        try {
                            mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                    mCaptureCallback, mBackgroundHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    int w = mTextureView.getWidth();
                    int h = mTextureView.getHeight();
                    if (R.id.radio_af == checkedId) {
                        focus_mode = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
                        focusSeek.setVisibility(View.GONE);
                        closeCamera();
                        openCamera(w, h);
                    } else {
                        focus_mode = CaptureRequest.CONTROL_AF_MODE_OFF;
                        focusSeek.setVisibility(View.VISIBLE);
                        closeCamera();
                        openCamera(w, h);
                    }
                });

                // Auto focus should be continuous for camera preview.
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, focus_mode);

                // Finally, we start displaying the camera preview.
                mPreviewRequest = mPreviewRequestBuilder.build();
                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                        mCaptureCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(
                @NonNull CameraCaptureSession cameraCaptureSession) {
            showToast("Failed");
        }
    };

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            // 원본 저장
            Image img = reader.acquireNextImage();
            mBackgroundHandler.post(new ImageSaver(img, mFile));

            // 이미지 크롭
            ByteBuffer buffer = img.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            startCropImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null));
        }
    };
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null || afState == 0) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }
    };
    //endregion Event listener

    private void showCrackDialog(String f_path, int type) {
        requireActivity().runOnUiThread(() -> {
            dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_crackinput);
            dialog.setCancelable(false);
            dialog.show();

            // Dialog 사이즈 조절 하기
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);

            //token
            SharedPreferences preferences = this.getActivity().getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
            String Token = "Bearer " + preferences.getString("token", "null");

            //user input data
            CrackData send_data = new CrackData();
            send_data.setImg(f_path);

//        Log.e("f_path: ", send_data.getImg());

            crack_position_sp = dialog.findViewById(R.id.crack_position_sp);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.subjectPosition, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            crack_position_sp.setAdapter(adapter);

            crack_position_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    send_data.setPosition(String.valueOf(adapterView.getAdapter().getItem(i)));
//                Log.e("dialog_spinner", String.valueOf(adapterView.getAdapter().getItem(i)));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    send_data.setPosition(String.valueOf(adapterView.getAdapter().getItem(0)));
                }
            });

            //도면 불러오기
            Intent intent = getActivity().getIntent();
            Bundle bundle = intent.getExtras();
            plan_id = bundle.getString("plan_id");

            planListBtn = dialog.findViewById(R.id.planListBtn);
            planListBtn.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    service.get_plan(Token, plan_id).enqueue(new Callback<PlanResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<PlanResponse> call, @NonNull Response<PlanResponse> response) {
                            PlanResponse result = response.body();
                            if (response.isSuccessful() && result != null) {
                                Log.e("get_plan: ", new Gson().toJson(result));
                                Intent intent = new Intent(getActivity().getApplicationContext(), PlanListImgActivity.class);
                                intent.putExtra("type", 1);
                                intent.putExtra("plan_img", result.getImg());
                                mStartForResult.launch(intent);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "해당 도면이 없습니다.", Toast.LENGTH_SHORT).show();
                                Log.e("get_plan: ", "해당 도면 데이터 없음");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PlanResponse> call, @NonNull Throwable t) {
                            Log.e("get_plan: ", t.getMessage());
                        }
                    });

                }
            });

            crackSize_edt = dialog.findViewById(R.id.crackSize_edt);
            crackLength_edt = dialog.findViewById(R.id.crackLength_edt);
            crack_etcEdt = dialog.findViewById(R.id.crack_etcEdt);

            size_ch = dialog.findViewById(R.id.size_ch);
            length_ch = dialog.findViewById(R.id.length_ch);
            planData_ch = dialog.findViewById(R.id.planData_ch);

            size_ch.setOnClickListener(v -> {
                if (size_ch.isChecked()) {
                    crackSize_edt.setText("-");
                    crackSize_edt.setHint("측정 불가");
                    crackSize_edt.setEnabled(false);
                } else {
                    crackSize_edt.setText(null);
                    crackSize_edt.setHint("폭");
                    crackSize_edt.setEnabled(true);
                }
            });

            length_ch.setOnClickListener(v -> {
                if (length_ch.isChecked()) {
                    crackLength_edt.setText("-");
                    crackLength_edt.setHint("측정 불가");
                    crackLength_edt.setEnabled(false);
                } else {
                    crackLength_edt.setText(null);
                    crackLength_edt.setHint("길이");
                    crackLength_edt.setEnabled(true);
                }
            });

            inputBtn = dialog.findViewById(R.id.inputBtn);
            inputBtn.setEnabled(false);
            inputBtn.setBackground(getResources().getDrawable(R.drawable.button_cancle));
            inputBtn.setOnClickListener(view -> {
                String crack_size;
                String crack_length;
                String crack_etc;

                crack_size = crackSize_edt.getText().toString();
                crack_length = crackLength_edt.getText().toString();

                if (crack_etcEdt.getText().toString().equals("")) {
                    crack_etc = "-";
//                Log.e("crack_etcEdt", "1");
                } else {
                    crack_etc = crack_etcEdt.getText().toString();
//                Log.e("crack_etcEdt", "2");
                }

                send_data.setSize(crack_size);
                send_data.setLength(crack_length);
//            send_data.setCrack_box_size(crack_area);
                send_data.setEtc(crack_etc);

                if (type == 1) {
                    Log.e("type", type + "");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    alertDialog = builder.setMessage("수정 하시겠습니까?")
                            .setPositiveButton("확인", (dialogInterface, i) -> {
                                //데이터 전송
                                SendCrackData(Token, send_data, type);
                                alertDialog.dismiss();
                                dialog.dismiss();
                                getActivity().finish();
                            })
                            .setNegativeButton("취소", (dialogInterface, i) -> {
                                alertDialog.dismiss();
                            })
                            .create();
                    alertDialog.show();
                } else {
                    //데이터 전송
                    SendCrackData(Token, send_data, type);
                    dialog.dismiss();
                    getActivity().finish();
                }
            });

            cancel_Btn = dialog.findViewById(R.id.cancel_Btn);
            cancel_Btn.setOnClickListener(view -> {
                dialog.dismiss();
            });
        });
    }

    public void SendCrackData(String Token, CrackData data, int type) {
        Bundle bundle = getArguments();
        CrackData requestData = null;

        if (bundle != null) {
            requestData = (CrackData) bundle.getSerializable("send_bundle");
        }

        Log.e("SendCrackData", "start");
        CrackRunnable crackRunnable = new CrackRunnable();
        crackRunnable.setToken(Token);
        crackRunnable.setData(data);
        crackRunnable.setType(type);
        crackRunnable.setRequestData(requestData);
        Thread thread = new Thread(crackRunnable);

        thread.start();
        loading_progress.show();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loading_progress.dismiss();
        Log.e("SendCrackData", Thread.currentThread().getName() + " end");
    }

    private class CrackRunnable implements Runnable {
        String token;
        int type;
        CrackData data;
        CrackData requestData;

        public void setToken(String token) {
            this.token = token;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setData(CrackData data) {
            this.data = data;
        }

        public void setRequestData(CrackData requestData) {
            this.requestData = requestData;
        }

        @Override
        public void run() {
            if (type == 1) {//update case
                Log.e("CrackRunnable: ", "run(): " + "update coordinate");
                try {
                    ResponseMessage result = service.patch_coordinate(token, String.valueOf(requestData.getCoordinateid()), String.valueOf(Math.round(x)), String.valueOf(Math.round(y))).execute().body();
                    Log.e("onResponse", new Gson().toJson(result));

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("onFailure", e.getMessage());
                    requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "좌표 수정 실패.", Toast.LENGTH_SHORT).show();
                            }
                    );
                }
                //update crackInfo
                thread3();

            } else {//input case
                HashMap<String, RequestBody> body = new HashMap<>();

                body.put("projectid", RequestBody.create(requestData.getProjectid(), MediaType.parse("text/plain")));
                body.put("planid", RequestBody.create(plan_id, MediaType.parse("text/plain")));
                body.put("x", RequestBody.create(String.valueOf(Math.round(x)), MediaType.parse("text/plain")));
                body.put("y", RequestBody.create(String.valueOf(Math.round(y)), MediaType.parse("text/plain")));

                try {
                    ResponseMessage result = service.post_coordinate(token, body).execute().body();
                    if (result != null) {
                        Log.e("onResponse", new Gson().toJson(result));
                        if (result.getMessage().equals("success")) {
                            thread2();
                        } else {
                            Log.e("onResponse", "좌표 등록 에러");
                            requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(getActivity(), "좌표 데이터 등록 오류. 재시도하세요.", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    }

                } catch (IOException e) {
                    Log.e("onFailure", e.getMessage());
                    e.printStackTrace();
                }
            }

            //cache clear
//            clearApplicationCache(null);
        }

        public void thread2() {//input crackInfo
            Log.e("thread2()", "getProjectid: " + requestData.getProjectid());

            File f = new File(data.getImg());

            RequestBody requestBody = RequestBody.create(f, MediaType.parse("multipart/form-data"));
            MultipartBody.Part f_img = MultipartBody.Part.createFormData("img", f.getName(), requestBody);

            Map<String, RequestBody> body = new HashMap<>();
            body.put("projectid", RequestBody.create(requestData.getProjectid(), MediaType.parse("text/plain")));
            body.put("planid", RequestBody.create(plan_id, MediaType.parse("text/plain")));
            body.put("x", RequestBody.create(String.valueOf(Math.round(x)), MediaType.parse("text/plain")));
            body.put("y", RequestBody.create(String.valueOf(Math.round(y)), MediaType.parse("text/plain")));
            body.put("zone", RequestBody.create(requestData.getZone(), MediaType.parse("text/plain")));
            body.put("position", RequestBody.create(data.getPosition(), MediaType.parse("text/plain")));
            body.put("size", RequestBody.create(data.getSize(), MediaType.parse("text/plain")));
            body.put("length", RequestBody.create(data.getLength(), MediaType.parse("text/plain")));
            body.put("type", RequestBody.create(requestData.getType(), MediaType.parse("text/plain")));
            body.put("etc", RequestBody.create(data.getEtc(), MediaType.parse("text/plain")));
            body.put("auto_analyze", RequestBody.create(requestData.getAuto_analyze(), MediaType.parse("text/plain")));

            try {
                CrackResponse result = service.post_crack_insert(token, f_img, body).execute().body();
                Log.e("onResponse", new Gson().toJson(result));
                requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "등록 완료", Toast.LENGTH_SHORT).show();
                        }
                );
                getActivity().finish();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("onFailure", e.getMessage());
                requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "등록 실패", Toast.LENGTH_SHORT).show();
                        }
                );
            }
        }

        public void thread3() {//update crackInfo
            Log.e("CrackRunnable: ", "thread3(): " + "update crack_data");
            File f = new File(data.getImg());

            RequestBody requestBody = RequestBody.create(f, MediaType.parse("multipart/form-data"));
            MultipartBody.Part f_img = MultipartBody.Part.createFormData("img", f.getName(), requestBody);

            Map<String, RequestBody> body = new HashMap<>();
            //현재 면적 컬럼 없어서 빠진 상태로 보내는 중
            body.put("crackid", RequestBody.create(String.valueOf(requestData.getCrackid()), MediaType.parse("text/plain")));
            body.put("zone", RequestBody.create(requestData.getZone(), MediaType.parse("text/plain")));
            body.put("position", RequestBody.create(data.getPosition(), MediaType.parse("text/plain")));
            body.put("type", RequestBody.create(requestData.getType(), MediaType.parse("text/plain")));
            body.put("size", RequestBody.create(data.getSize(), MediaType.parse("text/plain")));
            body.put("length", RequestBody.create(data.getLength(), MediaType.parse("text/plain")));
            body.put("etc", RequestBody.create(data.getEtc(), MediaType.parse("text/plain")));
            body.put("auto_analyze", RequestBody.create(requestData.getAuto_analyze(), MediaType.parse("text/plain")));

            try {
                ResponseMessage result = service.post_crack_update(token, f_img, body).execute().body();
                Log.e("onResponse", new Gson().toJson(result));
                requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "수정 완료", Toast.LENGTH_SHORT).show();
                        }
                );
                getActivity().finish();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("onFailure", e.getMessage());
                requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "수정 실패", Toast.LENGTH_SHORT).show();
                        }
                );
            }
        }
    }

    private String getURLForResource(int resId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resId).toString();
    }

    //cache clear
    private void clearApplicationCache(java.io.File dir) {
        if (dir == null)
            dir = getActivity().getCacheDir();
        if (dir == null)
            return;
        java.io.File[] children = dir.listFiles();
        try {
            for (java.io.File child : children)
                if (child.isDirectory())
                    clearApplicationCache(child);
                else child.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}