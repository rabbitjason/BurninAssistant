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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bugsbunny.burninassistant.bean.MusicBean;
import com.bugsbunny.burninassistant.manager.PreferenceManager;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/12/16.
 */
public class MusicActivity extends BaseActivity implements View.OnClickListener {

    private static final int ADD_MUSIC = 1;

    private List<MusicBean> musicList;// = new ArrayList<MusicBean>();
    MusicAdapter musicAdapter;
    private ListView lvMusic;
    private TextView tvAdd, tvReturn;

    public static MusicBean stMusic;
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

    public static void actionStart(Context context, int requestCode) {
        Intent intent = new Intent(context, MusicActivity.class);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    private void initView() {
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        tvAdd.setOnClickListener(this);

        tvReturn = (TextView) findViewById(R.id.tvReturn);
        tvReturn.setOnClickListener(this);
    }

    private void initMusicList() {
        musicList = PreferenceManager.getMusics();
        lvMusic = (ListView) findViewById(R.id.lvMusic);
        musicAdapter = new MusicAdapter(MusicActivity.this,
                R.layout.music_list_item, musicList);
        lvMusic.setAdapter(musicAdapter);
        lvMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicAdapter.select(position);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAdd:
                Intent audioPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                audioPickerIntent.setType("audio/*");
                startActivityForResult(audioPickerIntent, ADD_MUSIC);
                break;
            case R.id.tvReturn:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.saveMusics(musicList);
        super.onDestroy();
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

        // 选中当前选项时，让其他选项不被选中
        public void select(int position) {
            MusicBean music = getItem(position);
//            stMusic = music;
            if (!music.getSelected()) {
                music.setSelected(true);
                for (int i = 0; i < getCount(); i++) {
                    if (i != position) {
                        getItem(i).setSelected(false);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MusicBean music = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (null == convertView) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) view.findViewById(R.id.tvName);
                viewHolder.detail = (TextView) view.findViewById(R.id.tvDetail);
                viewHolder.btnSelected = (RadioButton) view.findViewById(R.id.btnSelected);
                viewHolder.btnSelected.setClickable(false);
                viewHolder.ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
                if ( 0 == position ) {
                    viewHolder.ivDelete.setVisibility(View.GONE);
                } else if ( 1 == position ) {
                    viewHolder.ivDelete.setVisibility(View.GONE);
                }
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
            viewHolder.btnSelected.setChecked(music.getSelected());
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(getItem(position));
                    notifyDataSetChanged();
                }
            });

            if (music.getSelected()) {
                stMusic = music;
            }

            return view;
        }

        class ViewHolder {
            TextView name;
            TextView detail;
            RadioButton btnSelected;
            ImageView ivDelete;
        }
    }
}
