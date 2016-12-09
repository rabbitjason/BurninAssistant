package com.bugsbunny.burninassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
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

import java.io.IOException;
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

        initMusicList();
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

    private void initMusicList() {
        MusicBean musicWhite = new MusicBean();
        musicWhite.setName("白噪音");
        musicWhite.setAlbum("煲机助手");
        musicWhite.setUrl("coicoi.wav");
        musicWhite.setIsAssetType(true);
        getMP3MetaData(musicWhite);
        musicList.add(musicWhite);

        MusicBean musicRed = new MusicBean();
        musicRed.setName("粉红噪音");
        musicRed.setAlbum("煲机助手");
        musicRed.setUrl("beep.wav");
        musicRed.setIsAssetType(true);
        getMP3MetaData(musicRed);
        musicList.add(musicRed);

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
            if (music.getIsAssetType()) {

                try {
                    AssetFileDescriptor afd = getAssets().openFd(music.getUrl());
                    retriever.setDataSource(afd.getFileDescriptor(),
                            afd.getStartOffset(), afd.getLength());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                retriever.setDataSource(music.getUrl());
            }

            //时长
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duration != null) {
                music.setLength(Long.parseLong(duration));
            }

            //艺术家
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (artist != null) {
                music.setArtist(artist);
            }

            //标题
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if (title != null) {
                music.setName(title);
            }

            //专辑
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            if (album != null) {
                music.setAlbum(album);
            }

            String author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);


        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    private String getRealPathFromURI(Uri contentUri) {
        String res = null;
        res = contentUri.toString();
        //返回文件的绝对路径
        if (res.contains("file://")) {
            return contentUri.getPath();
        }

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }

        cursor.close();

        return res;
    }

//    public String getRealPathFromURI(Uri contentUri) {
//        String res = null;
//        String[] proj = {MediaStore.Audio.Media.DATA};
//        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
//        if (cursor.moveToFirst()) {
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//            res = cursor.getString(column_index);
//        }
//        cursor.close();
//        return res;
//    }

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
            String detail = "";
            if (music.getArtist() != null && music.getAlbum() != null) {
                detail += music.getArtist() + " - " + music.getAlbum();
            } else if (music.getArtist() != null) {
                detail = music.getArtist();
            } else if (music.getAlbum() != null) {
                detail = music.getAlbum();
            }

            viewHolder.detail.setText(hms + " " + detail);

            return view;
        }

        class ViewHolder {
            TextView name;
            TextView detail;
        }
    }
}
