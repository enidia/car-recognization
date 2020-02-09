package com.example.demo.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "history")
public class HistoryLog implements Serializable {
    private static final long serialVersionUID = -108907189034815108L;

    @Id
    @GeneratedValue
    private int logId;

    String userName;
    String imageUrl;
    String result;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime identifyDate;

    public HistoryLog() {
    }

    public HistoryLog(String userName, String imageUrl, String result, LocalDateTime identifyDate) {
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.result = result;
        this.identifyDate = identifyDate;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getIdentifyDate() {
        return identifyDate;
    }

    public void setIdentifyDate(LocalDateTime identifyDate) {
        this.identifyDate = identifyDate;
    }
}
