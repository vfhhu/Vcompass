package xyz.vfhhu.compass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    SensorManager mSensorManager;
    float val_ACCELEROMETER[];
    float val_MAGNETIC_FIELD[];
    Sensor acc_sensor,mag_sensor;
    ImageView img_compass;
    TextView text_compass,text_compass_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        acc_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        img_compass=findViewById(R.id.img_compass);
        text_compass=findViewById(R.id.text_compass);
        text_compass_image=findViewById(R.id.text_compass_image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mSensorManager.registerListener(sensor_callback, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(sensor_callback, mag_sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mSensorManager.unregisterListener(sensor_callback);
        }
    }

    SensorEventCallback sensor_callback=new SensorEventCallback() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            super.onSensorChanged(event);
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)val_ACCELEROMETER=event.values.clone();
            if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)val_MAGNETIC_FIELD=event.values.clone();
            if(val_ACCELEROMETER!=null && val_MAGNETIC_FIELD!=null){
                getOrientation();
            }
        }
    };
    public float getOrientation(){
        if(val_ACCELEROMETER==null || val_MAGNETIC_FIELD==null)return 0;
        float va[]=val_ACCELEROMETER;
        float vm[]=val_MAGNETIC_FIELD;
        float []values=new float[9];
        float []R=new float[9];
        boolean success = SensorManager.getRotationMatrix(R,null,va,vm);
        if(success){
            SensorManager.getOrientation(R,values);
            float orientation=(float) Math.toDegrees(values[0]);
            if(orientation<0)orientation=360+orientation;

            float pitch=(float) Math.toDegrees(values[1])+360%360;
            float roll=(float) Math.toDegrees(values[2])+360%360;
            updateView(orientation,pitch,roll);
            return orientation;
        }
        return 0;
    }
    public void updateView(float rr,final float pitch,final float roll){
        final float r=rr;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text_compass.setText(String.format("%.2f", r));
                if(pitch>=135 || pitch<=225 || roll>=135 || roll<=225){
                     img_compass.setRotation((r+90));
                }else{
                    img_compass.setRotation((r+270)*-1);
                }
//                text_compass_image.setText(String.format("%.2f", ((r+270)*-1)));
            }
        });
    }



}