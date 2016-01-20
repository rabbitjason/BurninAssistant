package com.bugsbunny.burninassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bugsbunny.burninassistant.bean.MusicBean;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/16.
 */
public class MusicActivity extends BaseActivity implements View.OnClickListener {

    private static final int ADD_MUSIC = 1;

    private List<MusicBean> musicList = new ArrayList<MusicBean>();
    private ListView lvMusic;
    private TextView tvAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initView();
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MusicActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        lvMusic = (ListView) findViewById(R.id.lvMusic);

        tvAdd = (TextView) findViewById(R.id.tvAdd);
        tvAdd.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAdd:
                Intent audioPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                audioPickerIntent.setType("audio/*");
                startActivityForResult(audioPickerIntent, ADD_MUSIC);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (RESULT_OK == resultCode) {
            if (ADD_MUSIC == requestCode) {
                Uri uri = (Uri) data.getData();
                String path = getRealPathFromURI(uri);
                MusicBean music = new MusicBean();
                music.setUrl(path);
                getMP3MetaData(music);
                musicList.add(music);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getMP3MetaData(MusicBean music)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(music.getUrl());
            //时长
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            music.setLength(Long.parseLong(duration));

            //艺术家
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            music.setArtist(artist);

            //标题
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            music.setName(title);

            //专辑
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            music.setAlbum(album);


            String author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);


        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public class MusicAdapter extends ArrayAdapter<MusicBean> {

        private int resourceId;

        public MusicAdapter(Context context, int resource, List<MusicBean> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }
}
