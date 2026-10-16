package com.example.maxlab3.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * ที่เก็บข้อมูลของแอป (Singleton) - เก็บ User ทั้งหมด โดยโน้ตเก็บอยู่ใน User.userNotes
 * ทำหน้าที่เป็น "ผู้ส่งอีเวนต์": เมื่อข้อมูลเปลี่ยนจะแจ้ง OnDataChangedListener ทุกตัวที่ลงทะเบียนไว้
 */
public class DataStore {

    /** Event: ข้อมูล User/Note มีการเปลี่ยนแปลง */
    public interface OnDataChangedListener {
        void onDataChanged();
    }

    private static DataStore instance;

    private final List<User> users = new ArrayList<>();
    private final List<OnDataChangedListener> listeners = new ArrayList<>();

    private DataStore() { }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // ---------- Listener ----------
    public void addListener(OnDataChangedListener l) {
        if (l != null && !listeners.contains(l)) listeners.add(l);
    }

    public void removeListener(OnDataChangedListener l) {
        listeners.remove(l);
    }

    private void notifyDataChanged() {
        for (OnDataChangedListener l : new ArrayList<>(listeners)) {
            l.onDataChanged();
        }
    }

    // ---------- User ----------
    public User createUser(String username, String email, String passwordHash) {
        String id = String.format(Locale.US, "U%03d", users.size() + 1);
        User user = new User(id, username, email, passwordHash);
        users.add(user);
        notifyDataChanged();
        return user;
    }

    public boolean isUsernameTaken(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return true;
        }
        return false;
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    // ---------- Note ----------
    public void addNote(User owner, Note note) {
        owner.addNote(note);
        notifyDataChanged();
    }

    /** โน้ตของทุก User เรียงจากใหม่ไปเก่า */
    public List<Note> getAllNotes() {
        List<Note> all = new ArrayList<>();
        for (User u : users) {
            all.addAll(u.getUserNotes());
        }
        Collections.sort(all, (a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));
        return all;
    }
}
