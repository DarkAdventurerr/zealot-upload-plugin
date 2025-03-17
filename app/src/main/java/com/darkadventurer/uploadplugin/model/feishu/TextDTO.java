package com.darkadventurer.uploadplugin.model.feishu;

import com.google.gson.annotations.SerializedName;

/**
 * @author DarkAdventurer
 */
public class TextDTO {

    @SerializedName("content")
    private String content;
    @SerializedName("tag")
    private String tag;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
