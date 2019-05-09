package com.example.rkaudioplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.perfectaudioplayer.OnCompletionListener;
import com.example.perfectaudioplayer.RKAudioPlayerWidget;

public class DemoMainActivity extends AppCompatActivity {
   private RKAudioPlayerWidget apPerfect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_main);

        Toast.makeText(this, "Perfect RKVideoPlayer.....", Toast.LENGTH_LONG).show();

        apPerfect=findViewById(R.id.apPerfect);

        String songTitle = "Perfect Streaming Mp3 Music...";
        String audioUrl = "http://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3";

        //String songTitle = "Streaming Cool Mp3 Music...";
        //String audioUrl = "http://www.hochmuth.com/mp3/Haydn_Cello_Concerto_D-1.mp3";

        apPerfect.initSong(audioUrl,songTitle);

        apPerfect.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onComplete() {
                Toast.makeText(DemoMainActivity.this, "Video Completed....Show Cfu....", Toast.LENGTH_LONG).show();
            }
        });
    }
}
