package com.bugsbunny.burninassistant.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by lipple-server on 16/12/27.
 */

@AVClassName(Update.UPDATE_CLASSNAME)
public class Update extends AVObject {

    public static final String UPDATE_CLASSNAME = "Update";

    private static final String VERSION = "version";
    private static final String DESCRIPTION = "description";
    private static final String URL = "url";

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
}
