package com.bugsbunny.burninassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bugsbunny.burninassistant.bean.MusicBean;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/16.
 */
public class MusicActivity extends BaseActivity implements View.OnClickListener {

    private static final int ADD_MUSIC = 1;

    private List<MusicBean> musicList = new ArrayList<MusicBean>();
    MusicAdapter musicAdapter;
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
        musicAdapter = new MusicAdapter(MusicActivity.this,
                R.layout.music_list_item, musicList);
        lvMusic.setAdapter(musicAdapter);

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
                musicAdapter.notifyDataSetChanged();
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
            MusicBean music = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (null == convertView) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) view.findViewById(R.id.tvName);
                viewHolder.detail = (TextView) view.findViewById(R.id.tvDetail);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.name.setText(music.getName());
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式。
            String hms = formatter.format(music.getLength());
            viewHolder.detail.setText(hms + " " + music.getArtist()+" - " +music.getAlbum());

            return view;
        }

        class ViewHolder {
            TextView name;
            TextView detail;
        }
    }
}
