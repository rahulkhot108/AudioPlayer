package com.example.rkaudioplayer;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

public class MusicPlayerActivity extends AppCompatActivity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnBufferingUpdateListener {

    private ImageButton btnPlay;
    private ImageButton btnBackward;
    private TextView songTitleLabel;
    private SeekBar songProgressBar;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private MediaPlayer audioPlayer;
    private Handler mHandler = new Handler();
    private String audioUrl = "";
    private Utilities utils;
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private BarVisualizer barVisualizer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        btnPlay = findViewById(R.id.btnPlay);
        btnBackward = findViewById(R.id.btnBackward);
        songTitleLabel = findViewById(R.id.songTitle);
        songCurrentDurationLabel = findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = findViewById(R.id.songTotalDurationLabel);
        songProgressBar = findViewById(R.id.songProgressBar);
        barVisualizer = findViewById(R.id.barVisualizer);

        //TODO: init MediaPlayer and play the audio
        // Mediaplayer
        audioPlayer = new MediaPlayer();
        utils = new Utilities();

        songProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        audioPlayer.setOnCompletionListener(this); // Important
        audioPlayer.setOnBufferingUpdateListener(this); //Important

        audioUrl = "http://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3";

        // By default play first song
        initSong();

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (audioPlayer.isPlaying()) {
                    if (audioPlayer != null) {
                        audioPlayer.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                } else {
                    // Resume song
                    if (audioPlayer != null) {
                        audioPlayer.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int currentPosition = audioPlayer.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if (currentPosition - seekBackwardTime >= 0) {
                    // forward song
                    audioPlayer.seekTo(currentPosition - seekBackwardTime);
                } else {
                    // backward to starting position
                    audioPlayer.seekTo(0);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            initSong();
        }
    }

    public void initSong() {
        try {
            audioPlayer.reset();
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            int audioSessionId = audioPlayer.getAudioSessionId();
            if (audioSessionId != AudioManager.ERROR) {
                barVisualizer.setAudioSessionId(audioPlayer.getAudioSessionId());
            }

            Uri audioFileUri = Uri.parse(audioUrl);
            audioPlayer.setDataSource(getApplicationContext(), audioFileUri);
            audioPlayer.prepare();

            String songTitle = "Streaming Sample Mp3 Music...";
            songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_play);
            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 5);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = audioPlayer.getDuration();
            long currentDuration = audioPlayer.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            Log.d("Progress", "" + progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 5);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = audioPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        audioPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {

        btnPlay.setImageResource(R.drawable.btn_play);
        Toast.makeText(this, "Show CFU Here", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUpdateTimeTask != null) {
            mHandler.removeCallbacks(mUpdateTimeTask);
        }

        audioPlayer.release();

        if (barVisualizer != null)
            barVisualizer.release();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        songProgressBar.setSecondaryProgress(percent);
    }
}