package com.jk.mytattooartist;

public class InstagramMedia {

    private String id , caption , mediaURL;

    public InstagramMedia(String id , String caption , String mediaURL){
        this.id = id;
        this.caption = caption;
        this.mediaURL = mediaURL;

    }
    public void setMediaURL(String url){
        this.mediaURL=url;

    }

    public String getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public String getMediaURL() {
        return mediaURL;
    }
}