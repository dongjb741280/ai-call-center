package com.voice9.core.entity;

import java.io.Serializable;

public class TaskPause implements Serializable {
    private Long id;
    private Long taskId;
    private Long pauseStartTime;
    private Long pauseEndTime;

    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getPauseStartTime() { return pauseStartTime; }
    public void setPauseStartTime(Long pauseStartTime) { this.pauseStartTime = pauseStartTime; }
    public Long getPauseEndTime() { return pauseEndTime; }
    public void setPauseEndTime(Long pauseEndTime) { this.pauseEndTime = pauseEndTime; }
}
