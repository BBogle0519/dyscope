package com.example.ascope_lite.Notice;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NoticeDTO implements Serializable {
    @SerializedName("id")
    int id;

    @SerializedName("title")
    String title;

    @SerializedName("tags")
    int tags;

    @SerializedName("updated_at")
    String updated_at;

    @SerializedName("author")
    String author;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTags() {
        return tags;
    }

    public void setTags(int tags) {
        this.tags = tags;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
