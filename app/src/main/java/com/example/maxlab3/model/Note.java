package com.example.maxlab3.model;

import java.util.Date;

@SuppressWarnings("unused")
public abstract class Note {
    private String title;
    private final Date createdDate;
    private User owner; // เจ้าของโน้ต (กำหนดตอน User.addNote / createNote)

    public Note(String title) {
        this.title = title;
        this.createdDate = new Date();
    }

    public abstract String getSummary();

    /** รายละเอียดเต็มของโน้ต ใช้แสดงใน Dialog (แต่ละชนิดโน้ตแสดงต่างกัน) */
    public abstract String getDetail();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
