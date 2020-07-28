package com.example.lo17notes;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaCodecList;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MediatorLiveData;

import com.example.lo17notes.db.NoteDB;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditNoteActivity extends ListActivity implements View.OnClickListener {
    //-1为添加日志的操作
    private int noteId = -1;
    private EditText etName;
    private EditText etContent;
    private MediaAdapter adapter;
    public static final String EXTRA_NOTE_ID = "noteId";
    public static final String EXTRA_NOTE_NAME = "noteName";
    public static final String EXTRA_NOTE_CONTENT = "noteContent";

    public static final int REQUEST_CODE_GET_PHOTO = 1;
    public static final int REQUEST_CODE_GET_VIDEO = 2;
    private NoteDB db;
    private String currentPath;
    private SQLiteDatabase database, dbWriter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);
        adapter = new MediaAdapter(this);
        db = new NoteDB(this);
        database = db.getReadableDatabase();
        dbWriter = db.getWritableDatabase();
        setListAdapter(adapter);
        etName = (EditText) findViewById(R.id.etName);
        etContent = (EditText) findViewById(R.id.etContent);
        noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);
        if (noteId > -1) {
            etName.setText(getIntent().getStringExtra(EXTRA_NOTE_NAME));
            etContent.setText(getIntent().getStringExtra(EXTRA_NOTE_CONTENT));
            Cursor c = database.query(NoteDB.TABLE_NAME_MEDIA, null,
                    NoteDB.COLUMN_NAME_MEDIA_OWNERID + "=?", new String[]{noteId + ""},
                    null, null, null);
            while (c.moveToNext()) {
                adapter.add(new MediaCeilData(c.getInt(c.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_ID)), c.getString(c.getColumnIndex(NoteDB.COLUMN_NAME_MEDIA_PATH))));
            }
            adapter.notifyDataSetChanged();//通知刷新
        }
        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnAddPhoto).setOnClickListener(this);
        findViewById(R.id.btnAddVideo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        File f;
        switch (v.getId()) {
            case R.id.btnSave:
                saveMedia(saveNote());
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btnAddPhoto:
                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                f = new File(getMediaDir(), System.currentTimeMillis() + ".jpg");
                Uri uri;
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                currentPath = f.getAbsolutePath();
                uri = FileProvider.getUriForFile(this,
                        getApplicationContext().getApplicationContext()
                                .getPackageName() + ".fileProvider", f);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, REQUEST_CODE_GET_PHOTO);
                break;
            case R.id.btnAddVideo:
                i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                f = new File(getMediaDir(), System.currentTimeMillis() + ".mp4");
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                currentPath = f.getAbsolutePath();
                //输出到指定Uri路径
                uri = FileProvider.getUriForFile(this,
                        getApplicationContext().getApplicationContext()
                                .getPackageName() + ".fileProvider", f);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, REQUEST_CODE_GET_VIDEO);

                break;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent;
        MediaCeilData data = adapter.getItem(position);
        switch (data.type) {
            case MediaType.PHOTO:
                Log.d("TAG", String.valueOf(data.type));
                intent = new Intent(this, AtyPhotoViewr.class);
                intent.putExtra(AtyPhotoViewr.EXTRA_PATH, data.path);
                startActivity(intent);
                break;
            case MediaType.VIDEO:
                intent = new Intent(this, AtyVideoViewer.class);
                intent.putExtra(AtyVideoViewer.EXTRA_PATH, data.path);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GET_PHOTO:
            case REQUEST_CODE_GET_VIDEO:
                if (resultCode == RESULT_OK) {
                    adapter.add(new MediaCeilData(currentPath));
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    public File getMediaDir() {
        //SD卡目录不存在就创建
        File dir = new File(Environment.getExternalStorageDirectory(), "NotesMedia");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    public int saveNote() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDB.COLUMN_NAME_NOTE_NAME, etName.getText().toString());
        contentValues.put(NoteDB.COLUMN_NAME_NOTE_CONTENT, etContent.getText().toString());
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = f.format(new Date());
        contentValues.put(NoteDB.COLUMN_NAME_NOTE_DATE, date);
        if (noteId > -1) {
            dbWriter.update(NoteDB.TABLE_NAME_NOTES, contentValues,
                    NoteDB.COLUMN_NAME_NOTE_ID + "=?", new String[]{noteId + ""});
            return noteId;
        } else {
            return (int) dbWriter.insert(NoteDB.TABLE_NAME_NOTES, null, contentValues);
        }
    }

    public void saveMedia(int noteId) {
        MediaCeilData data;
        ContentValues cv = new ContentValues();
        for (int i = 0; i < adapter.getCount(); i++) {
            data = adapter.getItem(i);
            if (data.id <= -1) {
                cv.put(NoteDB.COLUMN_NAME_MEDIA_PATH, data.path);
                cv.put(NoteDB.COLUMN_NAME_MEDIA_OWNERID, noteId);
                dbWriter.insert(NoteDB.TABLE_NAME_MEDIA, null, cv);
            }
        }
    }

    static class MediaAdapter extends BaseAdapter {
        private Context context;
        private List<MediaCeilData> list = new ArrayList<>();

        public MediaAdapter(Context context) {
            this.context = context;
        }

        public void add(MediaCeilData data) {
            list.add(data);

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MediaCeilData getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.media_list, parent, false);
            }
            MediaCeilData data = getItem(position);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ivIcon);
            TextView tvPath = (TextView) convertView.findViewById(R.id.tvPath);
            imageView.setImageResource(data.iconId);
            tvPath.setText(data.path);
            return convertView;
        }
    }

    static class MediaCeilData {
        int type;
        int id = -1;
        String path = "";
        int iconId = R.drawable.ic_launcher_background;

        public MediaCeilData(String path) {
            this.path = path;
            if (path.endsWith(".jpg")) {
                iconId = R.drawable.apple;
                type = MediaType.PHOTO;
            } else if (path.endsWith(".mp4")) {
                iconId = R.drawable.banana;
                type = MediaType.VIDEO;
            }
        }

        public MediaCeilData(int id, String path) {
            this.id = id;
            this.path = path;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        dbWriter.close();
    }

    static class MediaType {
        static final int PHOTO = 1;
        static final int VIDEO = 2;
    }
}
