package com.example.akshay.noblind;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nisrulz.sensey.ChopDetector;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
import com.github.nisrulz.sensey.WristTwistDetector;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public VisionServiceClient visionServiceClient= new VisionServiceRestClient("0bea8746fa9040179804d782c93172aa","https://westcentralus.api.cognitive.microsoft.com/vision/v1.0");

    private ImageView AnalyseImage;
    private ImageView ClickImage;
    String detailstr;

    private static int PICK_IMAGE=1;
    Bitmap photo;
    ByteArrayOutputStream outputStream;
    ImageView imageView;
    TextView textView;

    ByteArrayInputStream inputStream;
    TextToSpeech t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnalyseImage = (ImageView) findViewById(R.id.AnalyseImage);
        Sensey.getInstance().init(this);
        FirebaseApp.initializeApp(getApplicationContext());



        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);

        WristTwistDetector.WristTwistListener wristTwistListener=new WristTwistDetector.WristTwistListener() {
                @Override public void onWristTwist() {
                    // Wrist Twist gesture detected, do something
                    Intent j=new Intent(MainActivity.this,Emotions.class);
                    startActivity(j);
                }
            };

        Sensey.getInstance().startWristTwistDetection(wristTwistListener);


        ChopDetector.ChopListener chopListener=new ChopDetector.ChopListener() {
            @Override public void onChop() {
                // Chop gesture detected, do something
                Intent n=new Intent(MainActivity.this,BarcodeDetectorActivity.class);
                startActivity(n);
            }
        };


        Sensey.getInstance().startChopDetection(chopListener);


        ClickImage = (ImageView) findViewById(R.id.clickImage);
        t1 = new TextToSpeech(this, this);

    }





    public void clickImageClicked(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){




                photo = (Bitmap) data.getExtras().get("data");
                outputStream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG,5,outputStream);
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                //use the bitmap as you like



            AsyncTask<InputStream,String,String> visionTask= new AsyncTask<InputStream, String, String>() {

                @Override
                protected void onPostExecute(String s) {
                    AnalysisResult result=new Gson().fromJson(s,AnalysisResult.class);
                    StringBuilder stringBuilder=new StringBuilder();
                    for(Caption caption:result.description.captions){
                        stringBuilder.append(caption.text);
                    }

                    detailstr=stringBuilder.toString();


                        t1.speak(stringBuilder.toString(), TextToSpeech.QUEUE_FLUSH, null,null);
                        Log.i("message","Tester log message number 2");
                        Toast.makeText(getApplicationContext(), stringBuilder.toString(),Toast.LENGTH_SHORT).show();


                }

                @Override
                protected String doInBackground(InputStream... inputStreams) {
                    try{
                        String[] features={"Description"};
                        String[] details={};

                        AnalysisResult result=visionServiceClient.analyzeImage(inputStreams[0],features,details);
                        String str=new Gson().toJson(result);
                        return str;
                    } catch (Exception e) {
                        return null;
                    }


                }


            };
            visionTask.execute(inputStream);

        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onPause(){
        if(t1 !=null){
            t1.stop();
        }
        super.onPause();
    }

    public void AnalyseImageClicked(View view) {
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            t1.setLanguage(Locale.getDefault());
            t1.speak(detailstr, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            //Do something
            Intent z=new Intent(MainActivity.this,OcrCaptureActivity.class);
            startActivity(z);
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            //Do something
          Intent callIntent = new Intent(Intent.ACTION_CALL);
           callIntent.setData(Uri.parse("tel:08039515396"));
            startActivity(callIntent);

        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Sensey.getInstance().stop();
    }


}
