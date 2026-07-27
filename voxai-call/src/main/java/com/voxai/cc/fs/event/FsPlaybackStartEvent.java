package com.voxai.cc.fs.event;

import com.alibaba.fastjson.annotation.JSONField;
import com.voxai.cc.fs.event.base.FsBaseEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class FsPlaybackStartEvent extends FsBaseEvent {

    @JSONField(name = "variable_current_application")
    private String application;

    @JSONField(name = "Playback-File-Path")
    private String play;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    @Override
    public String toString() {
        return "FsPlaybackStartEvent{" +
                "application='" + application + '\'' +
                ", play='" + play + '\'' +
                '}';
    }
}
