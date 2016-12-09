package com.bugsbunny.burninassistant.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/12/10.
 */
public class MusicBean {
    private static final String NAME = "name";
    private static final String ARTIST = "artist";
    private static final String ALBUM = "album";
    private static final String URL = "url";
    private static final String LENGTH = "length";
    private static final String IS_ASSET_TYPE = "isAssetType";

    private String name;
    private String url;
    private String artist;
    private String album;
    private long length;

    private boolean isAssetType = false;

    public boolean getIsAssetType() {
        return  isAssetType;
    }

    public void setIsAssetType(boolean is) {
        this.isAssetType = is;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String toJSONString() {
        JSONObject jsonMusic = null;
        try {
            jsonMusic = new JSONObject();
            jsonMusic.put(NAME, name);
            jsonMusic.put(ARTIST, artist);
            jsonMusic.put(ALBUM, album);
            jsonMusic.put(URL, url);
            jsonMusic.put(LENGTH, length);
            jsonMusic.put(IS_ASSET_TYPE, isAssetType);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonMusic.toString();
    }

    public static MusicBean objectFromJSONString(String s) {
        JSONObject json = null;
        MusicBean music = null;
        try {
            json = new JSONObject(s);
            music = new MusicBean();
            if (json.has(NAME)) {
                music.setName(json.getString(NAME));
            }
            if (json.has(ARTIST)) {
                music.setArtist(json.getString(ARTIST));
            }
            if (json.has(ALBUM)) {
                music.setAlbum(json.getString(ALBUM));
            }
            if (json.has(URL)) {
                music.setUrl(json.getString(URL));
            }
            if (json.has(LENGTH)) {
                music.setLength(json.getLong(LENGTH));
            }
            if (json.has(IS_ASSET_TYPE)) {
                music.setIsAssetType(json.getBoolean(IS_ASSET_TYPE));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return music;
    }
}
