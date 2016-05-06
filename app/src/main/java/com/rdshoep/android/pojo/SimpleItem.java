package com.rdshoep.android.pojo;
/*
 * @description
 *   Please write the SimpleItem module's description
 * @author Zhang (rdshoep@126.com)
 *   http://www.rdshoep.com/
 * @version 
 *   1.0.0(5/5/2016)
 */

public class SimpleItem {
    String title;
    String description;
    Object extra;

    public SimpleItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public SimpleItem(String title, String description, Object extra) {
        this.title = title;
        this.description = description;
        this.extra = extra;
    }

    public String getTitle() {
        return title;
    }

    public SimpleItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SimpleItem setDescription(String description) {
        this.description = description;
        return this;
    }

    public Object getExtra() {
        return extra;
    }

    public SimpleItem setExtra(Object extra) {
        this.extra = extra;
        return this;
    }
}
