package com.example.lo17notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.lo17notes.db.NoteDB;

public class MainActivity extends ListActivity {
    private SimpleCursorAdapter adapter = null;
    private NoteDB db;//提供数据的
    private SQLiteDatabase dbRead;//读取数据的
    //编辑Note请求吗
    private static final int REQUEST_CODE_EDIT=2;
    //添加Note请求吗
    private static final int REQUEST_CODE_ADD_NOTE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new NoteDB(this);
        dbRead = db.getReadableDatabase();
        adapter = new SimpleCursorAdapter(this, R.layout.notes_list_ceil, null,
                new String[]{NoteDB.COLUMN_NAME_NOTE_NAME, NoteDB.COLUMN_NAME_NOTE_DATE},//从哪里取出
                new int[]{R.id.tvName, R.id.tvDate});//传到哪里
        setListAdapter(adapter);
        refreshNoteListView();
        findViewById(R.id.btnAddNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, EditNoteActivity.class), REQUEST_CODE_ADD_NOTE);
            }
        });
    }

    //上上一层活动返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ADD_NOTE:
            case REQUEST_CODE_EDIT:
                if (resultCode == Activity.RESULT_OK) {
                    refreshNoteListView();
                }
                break;
            default:
                break;
        }

    }
    //点击ListView某一项回调

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor=adapter.getCursor();
        cursor.moveToPosition(position);
        Intent intent =new Intent(MainActivity.this,EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.EXTRA_NOTE_ID, cursor.getInt(cursor.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_ID)));
        intent.putExtra(EditNoteActivity.EXTRA_NOTE_NAME,cursor.getString(cursor.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_NAME)));
        intent.putExtra(EditNoteActivity.EXTRA_NOTE_CONTENT,cursor.getString(cursor.getColumnIndex(NoteDB.COLUMN_NAME_NOTE_CONTENT)));
        startActivityForResult(intent,REQUEST_CODE_EDIT);
        super.onListItemClick(l, v, position, id);
    }

    public void refreshNoteListView() {
        //查询所有数据
        adapter.changeCursor(dbRead.query(NoteDB.TABLE_NAME_NOTES,
                null, null, null, null, null, null));

    }
}
