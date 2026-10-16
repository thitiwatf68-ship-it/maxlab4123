package com.example.maxlab3.controller;

import com.example.maxlab3.model.ChecklistNote;
import com.example.maxlab3.model.Note;
import com.example.maxlab3.model.TextNote;
import com.example.maxlab3.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Controller: ตัวกลางระหว่าง AddNoteActivity / MainActivity (View)
 * กับ TextNote, ChecklistNote, User (Model)
 *  - ตรวจสอบข้อมูลที่ View ส่งมา
 *  - สร้าง Model และสั่งเก็บลง User ผ่าน DataStore
 *  - ส่งข้อมูลโน้ตกลับให้ View แสดงผล
 */
public class NoteController extends BaseController {

    /** ผลลัพธ์ของการเพิ่มโน้ต ให้ View นำไปแสดงข้อความผิดพลาดเอง */
    public enum Error {
        NONE, NO_OWNER, TITLE_REQUIRED, NO_ITEMS
    }

    // ---------- เพิ่มโน้ต ----------
    public Error addTextNote(User owner, String title, String content) {
        Error error = validate(owner, title);
        if (error != Error.NONE) return error;

        TextNote note = new TextNote(title.trim(), content == null ? "" : content.trim());
        store.addNote(owner, note);
        return Error.NONE;
    }

    public Error addChecklistNote(User owner, String title, List<String> items) {
        Error error = validate(owner, title);
        if (error != Error.NONE) return error;

        ChecklistNote note = new ChecklistNote(title.trim());
        if (items != null) {
            for (String item : items) {
                if (item != null && !item.trim().isEmpty()) {
                    note.addItem(item.trim());
                }
            }
        }
        if (note.getCheckList().isEmpty()) return Error.NO_ITEMS;

        store.addNote(owner, note);
        return Error.NONE;
    }

    private Error validate(User owner, String title) {
        if (owner == null) return Error.NO_OWNER;
        if (title == null || title.trim().isEmpty()) return Error.TITLE_REQUIRED;
        return Error.NONE;
    }

    // ---------- แสดงโน้ต ----------
    /** โน้ตของทุก User เรียงจากใหม่ไปเก่า (แต่ละโน้ตรู้จักเจ้าของผ่าน getOwner()) */
    public List<Note> getAllNotes() {
        return store.getAllNotes();
    }

    /** โน้ตเฉพาะของ User คนเดียว */
    public List<Note> getNotesOf(User user) {
        if (user == null) return new ArrayList<>();
        return Collections.unmodifiableList(user.getUserNotes());
    }

    // ---------- Multitasking (Thread) ----------
    /** เวลาหน่วงจำลองการโหลดข้อมูล */
    public static final long LOAD_DELAY_MS = 2000;

    /** Callback ถูกเรียกจาก Worker Thread - View ต้องใช้ runOnUiThread ก่อนแตะ UI */
    public interface Callback<T> {
        void onResult(T result);
    }

    /** จำลองโหลดข้อมูลหน้า Browse: ทำงานใน Thread แยก หน่วง 2 วินาที แล้วแจ้งผล */
    public void loadBrowseData(Callback<Boolean> callback) {
        Thread worker = new Thread(() -> {
            try {
                Thread.sleep(LOAD_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (callback != null) callback.onResult(true);
        }, "LoadBrowseDataThread");
        worker.start();
    }

    /** ค้นหาโน้ตของ User ตามชื่อ: ทำงานใน Thread แยก หน่วง 2 วินาที แล้วส่งรายการที่พบ (อาจว่าง) */
    public void searchNotesByUser(String username, Callback<List<Note>> callback) {
        final String keyword = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        Thread worker = new Thread(() -> {
            try {
                Thread.sleep(LOAD_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            List<Note> found = new ArrayList<>();
            for (User u : store.getUsers()) {
                if (u.getUsername().toLowerCase(Locale.ROOT).contains(keyword)) {
                    found.addAll(u.getUserNotes());
                }
            }
            if (callback != null) callback.onResult(found);
        }, "SearchNotesThread");
        worker.start();
    }

    public boolean isValidKeyword(String keyword) {
        return keyword != null && !keyword.trim().isEmpty();
    }
}
