/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csc229labfiles.finalaudioplayer;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author itlabs
 */

public class MusicFile {

    public MusicFile(String songtitle, String Artist, String FileName) {
        this.songtitle = songtitle;
        this.Artist = Artist;
        this.FileName = FileName;
    }

    @Override
    public String toString() {
        return "MusicFile{" + "songtitle=" + songtitle + ", Artist=" + Artist + ", FileName=" + FileName + '}';
    }
     @SerializedName("Songtitle")
    private String songtitle;
    @SerializedName("price")
    private String Artist;
    @SerializedName("FileName")
    private String FileName ;

    public String getSongtitle() {
        return songtitle;
    }

    public void setSongtitle(String songtitle) {
        this.songtitle = songtitle;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String Artist) {
        this.Artist = Artist;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }
    
  
     }
    
    

