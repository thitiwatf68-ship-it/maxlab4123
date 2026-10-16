package com.example.maxlab3.controller;

import com.example.maxlab3.model.DataStore;

/**
 * Controller พื้นฐาน - ให้ View ลงทะเบียนรับการแจ้งเตือนเมื่อ Model เปลี่ยน
 * (Model -> แจ้ง -> View รีเฟรชตัวเอง) โดยไม่ต้องเข้าถึง DataStore โดยตรง
 */
public abstract class BaseController {

    protected final DataStore store = DataStore.getInstance();

    public void addListener(DataStore.OnDataChangedListener listener) {
        store.addListener(listener);
    }

    public void removeListener(DataStore.OnDataChangedListener listener) {
        store.removeListener(listener);
    }
}
