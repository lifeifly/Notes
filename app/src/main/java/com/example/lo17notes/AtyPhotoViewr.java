package com.example.lo17notes;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
//显示图片
public class AtyPhotoViewr extends Activity {
    public static final String EXTRA_PATH="path";
    private ImageView iv;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        iv=new ImageView(this);
        setContentView(iv);
        String path=getIntent().getStringExtra(EXTRA_PATH);
        if (path!=null){
            iv.setImageURI(Uri.parse(path));
        }else {
            finish();
        }
    }

}
