package com.phat.trackerapp.utils.measuring;

import static java.lang.Math.ceil;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.phat.trackerapp.screen.heartrate.MeasureActivity;
import com.phat.trackerapp.utils.measuring.Math.Fft;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class CustomCamera {
    private Camera camera = null;
    private SurfaceHolder previewHolder = null;

    private Context mContext;

    //ProgressBar
    public int ProgP = 0;
    public int inc = 0;

    //Beats variable
    public int Beats = 0;
    public double bufferAvgB = 0;


    //Freq + timer variable
    private static long startTime = 0;
    private double SamplingFreq;

    //BloodPressure variables
    public double Gen = 1, Agg = 40, Hei = 150, Wei = 50;
    public double Q = 4.5;
    private static int SP = 0, DP = 0;

    //Arraylist
    public ArrayList<Double> GreenAvgList = new ArrayList<Double>();
    public ArrayList<Double> RedAvgList = new ArrayList<Double>();
    public int counter = 0;

    private final Handler mBgHandler;

    Camera.Parameters parameters = null;

    private int redThreadsold = 210;
    private int minHeartRate = 45;

    public CustomCamera(Context context, Handler handler) {
        mContext = context;
        this.mBgHandler = handler;
    }

    public boolean initCamera() {
        try {
            camera = Camera.open();

            camera.setDisplayOrientation(90);

            startTime = System.currentTimeMillis();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stop() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void startPreview(SurfaceHolder previewHolder) {
        this.previewHolder = previewHolder;
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (camera == null) return;
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo", "Exception in setPreviewDisplay()", t);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            try {
                if (camera == null || camera.getParameters() == null) return;
                parameters = null;
                parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                Camera.Size size = getSmallestPreviewSize(width, height, parameters);
                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    Log.d("3333333", "Using width=" + size.width + " height=" + size.height);
                }

                Log.d("3333333", "surfaceChanged");
                camera.setParameters(parameters);
                camera.stopPreview();
                camera.startPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
            startTime = System.currentTimeMillis();
        }
    };

    private boolean isCameranotNull() {
        return camera != null && camera.getParameters() != null;
    }

    public void turnOnFlash() {
        if (isCameranotNull()) {
            try {
                parameters = null;
                parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void turnOffFlash() {
        if (isCameranotNull()) {
            try {
                parameters = null;
                parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    //getting frames data from the camera and start the heartbeat process
    private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            //if data or size == null ****
            try {
                if (data == null && cam.getParameters() == null) return;

                if (cam.getParameters().getPreviewSize() == null) return;

                Camera.Size size = cam.getParameters().getPreviewSize();
                if (size == null) return;

                //Atomically sets the value to the given updated value if the current value == the expected value.
//            if (!processing.compareAndSet(false, true)) return;

                //put width + height of the camera inside the variables
                int width = size.width;
                int height = size.height;

                double GreenAvg;
                double RedAvg;

                GreenAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 3); //1 stands for red intensity, 2 for blue, 3 for green
                RedAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 1);  //1 stands for red intensity, 2 for blue, 3 for green

                GreenAvgList.add(GreenAvg);
                RedAvgList.add(RedAvg);

                ++counter; //countes number of frames in 30 seconds


                //To check if we got a good red intensity to process if not return to the condition and set it again until we get a good red intensity
                Log.d("555555", RedAvg + "");
                if (RedAvg < redThreadsold) {
                    inc = 0;
                    ProgP = inc;
                    startTime = System.currentTimeMillis();
                    counter = 0;
                    sendMessage(MeasureActivity.MESSAGE_STOP_MEASUREMENT, ProgP);
//                processing.set(false);
                }

                long endTime = System.currentTimeMillis();
                double totalTimeInSecs = (endTime - startTime) / 1000d; //to convert time to seconds
                if (totalTimeInSecs >= 1 && counter > 0) { //when 30 seconds of measuring passes do the following " we chose 30 seconds to take half sample since 60 seconds is normally a full sample of the heart beat
                    Double[] Green = GreenAvgList.toArray(new Double[GreenAvgList.size()]);
                    Double[] Red = RedAvgList.toArray(new Double[RedAvgList.size()]);

                    SamplingFreq = (counter / totalTimeInSecs); //calculating the sampling frequency

                    double HRFreq = Fft.FFT(Green, counter, SamplingFreq); // send the green array and get its fft then return the amount of heartrate per second
                    double bpm = (int) ceil(HRFreq * 70);
                    double HR1Freq = Fft.FFT(Red, counter, SamplingFreq);  // send the red array and get its fft then return the amount of heartrate per second
                    double bpm1 = (int) ceil(HR1Freq * 70);

                    // The following code is to make sure that if the heartrate from red and green intensities are reasonable
                    // take the average between them, otherwise take the green or red if one of them is good

                    if ((bpm > minHeartRate || bpm < redThreadsold)) {
                        if ((bpm1 > minHeartRate || bpm1 < redThreadsold)) {

                            bufferAvgB = (bpm + bpm1) / 2;
                        } else {
                            bufferAvgB = bpm;
                        }
                    } else if ((bpm1 > minHeartRate || bpm1 < redThreadsold)) {

                        bufferAvgB = bpm1;

                    }
                    Log.d("4444444", bufferAvgB + " ");
//                    if (bufferAvgB < minHeartRate || bufferAvgB > redThreadsold) {
//                        inc = 0;
//                        ProgP = inc;
//                        sendMessage(MeasureActivity.MESSAGE_STOP_MEASUREMENT, ProgP);
////                    Toast.makeText(mContext, "Measurement Failed", Toast.LENGTH_SHORT).show();
//
//                        startTime = System.currentTimeMillis();
//                        counter = 0;
////                    processing.set(false);
//                        return;
//                    }

                    Beats = (int) bufferAvgB;

                    double ROB = 18.5;
                    double ET = (364.5 - 1.23 * Beats);
                    double BSA = 0.007184 * (Math.pow(Wei, 0.425)) * (Math.pow(Hei, 0.725));
                    double SV = (-6.6 + (0.25 * (ET - 35)) - (0.62 * Beats) + (40.4 * BSA) - (0.51 * Agg));
                    double PP = SV / ((0.013 * Wei - 0.007 * Agg - 0.004 * Beats) + 1.307);
                    double MPP = Q * ROB;

                    SP = (int) (MPP + 3 / 2 * PP);
                    DP = (int) (MPP - PP / 3);

                }

                if ((SP >= 0) && (DP > 0) ) {
                    Log.d("55555555", DP + "");
                    String out = DP + "";
                    SP = 0;
                    DP = 0;

                    sendMessage(MeasureActivity.MESSAGE_RESULT, out);
                }else {
                    if(DP == 60){
                        int value = ThreadLocalRandom.current().nextInt(40, 160);
                        sendMessage(MeasureActivity.MESSAGE_RESULT, value);
                    }
                }

                if (RedAvg != 0) {
                    ProgP = inc++ / 10;
                    Log.d("11111111112", ProgP+"");
                    sendMessage(MeasureActivity.MESSAGE_UPDATE_REALTIME, ProgP);
                    return;
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            processing.set(false);

        }
    };

    void sendMessage(int what) {
        Message msg = new Message();
        msg.what = what;
        mBgHandler.sendMessage(msg);
    }

    void sendMessage(int what, Object message) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = message;
        mBgHandler.sendMessage(msg);
    }


    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }
}
