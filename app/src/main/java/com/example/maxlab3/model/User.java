package com.example.maxlab3.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class User {
    private final String userId;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final Date createdAt;
    private final List<Note> userNotes;

    public User(String userId, String username, String email, String passwordHash) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = new Date();
        this.userNotes = new ArrayList<>();
    }

    public boolean register() {
        return true;
    }

    public boolean login() {
        return true;
    }

    public TextNote createNote(String title, String content) {
        TextNote newNote = new TextNote(title, content);
        newNote.setOwner(this);
        userNotes.add(newNote);
        return newNote;
    }

    public void addNote(Note note) {
        if (note != null) {
            note.setOwner(this);
            userNotes.add(note);
        }
    }

    public List<Note> getUserNotes() {
        return userNotes;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Date getCreatedAt() { return createdAt; }
}