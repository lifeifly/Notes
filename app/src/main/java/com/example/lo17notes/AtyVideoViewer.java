package com.example.lo17notes;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;

public class AtyVideoViewer extends Activity {
    private VideoView videoView;
    public static final String EXTRA_PATH="path";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoView=new VideoView(this);
        videoView.setMediaController(new MediaController(this));
        setContentView(videoView);
        String path= getIntent().getStringExtra(EXTRA_PATH);
        if (path!=null){
            videoView.setVideoPath(path);
        }else {
            finish();
        }
    }

}
