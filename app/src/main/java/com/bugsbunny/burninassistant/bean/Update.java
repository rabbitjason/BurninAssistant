package com.bugsbunny.burninassistant.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;

/**
 * Created by lipple-server on 16/12/27.
 */

@AVClassName(Update.UPDATE_CLASSNAME)
public class Update extends AVObject {

    public static final String UPDATE_CLASSNAME = "Update";

    private static final String VERSION = "version";
    private static final String DESCRIPTION = "description";
    private static final String URL = "url";
    private static final String CREATED_AT = "createdAt";

    private String version;
    private String description;
    private String url;

    public String getVersion()
    {
        return this.getString(VERSION);
    }

    public void setVersion(String version)
    {
        this.put(VERSION, version);
    }

    public String getDescription()
    {
        return this.getString(DESCRIPTION);
    }

    public void setDescription(String description)
    {
        this.put(DESCRIPTION, description);
    }

    public String getUrl()
    {
        return this.getString(URL);
    }

    public void setUrl(String url)
    {
        this.put(URL, url);
    }

    public interface GetUpdateCallback {
        void done(Update update, AVException e);
    }

    public static void getTheLastestUpdate(final GetUpdateCallback callback) {
        AVQuery<Update> query = AVQuery.getQuery(Update.class);
        query.addDescendingOrder(CREATED_AT);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600);
        query.getFirstInBackground(new GetCallback<Update>() {
            @Override
            public void done(Update update, AVException e) {
                if (null == e) {
                    callback.done(update, e);
                } else {
                    if (e.getCode() == AVException.CACHE_MISS) {
                        return;
                    } else if (e.getCode() == AVException.INTERNAL_SERVER_ERROR) {
                        callback.done(null, e);
                    }
                }
            }
        });
    }
}
