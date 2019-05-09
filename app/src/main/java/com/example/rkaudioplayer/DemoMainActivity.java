package com.example.rkaudioplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.perfectaudioplayer.OnCompletionListener;
import com.example.perfectaudioplayer.PerfectAudioPlayer;

import java.util.ArrayList;
import java.util.List;

public class DemoMainActivity extends AppCompatActivity {

    private PerfectAudioPlayer apPerfect;

    String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_main);

        Toast.makeText(this, "Perfect RKVideoPlayer.....", Toast.LENGTH_LONG).show();

        apPerfect = findViewById(R.id.apPerfect);

        String songTitle = "Perfect Streaming Mp3 Music...";
        String audioUrl = "http://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3";

        //String songTitle = "Streaming Cool Mp3 Music...";
        //String audioUrl = "http://www.hochmuth.com/mp3/Haydn_Cello_Concerto_D-1.mp3";

        if (checkPermissions()) {
            apPerfect.initSong(audioUrl, songTitle);
        }

        apPerfect.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onComplete() {
                Toast.makeText(DemoMainActivity.this, "Video Completed....Show Cfu....", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                checkPermissions();
            }
        }
    }
}
