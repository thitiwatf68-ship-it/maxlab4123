package com.example.maxlab3.model;

import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class ChecklistNote extends Note {
    private List<String> checkList;

    public ChecklistNote(String title) {
        super(title);
        this.checkList = new ArrayList<>();
    }

    public ChecklistNote(String title, List<String> checkList) {
        super(title);
        this.checkList = checkList;
    }

    @Override
    public String getSummary() {
        int totalItems = checkList != null ? checkList.size() : 0;
        return getTitle() + ": [" + totalItems + " items in checklist]";
    }

    @Override
    public String getDetail() {
        if (checkList == null || checkList.isEmpty()) {
            return "(ไม่มีรายการ)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < checkList.size(); i++) {
            if (i > 0) sb.append("\n");
            sb.append("\u2022 ").append(checkList.get(i));
        }
        return sb.toString();
    }

    public void addItem(String item) {
        this.checkList.add(item);
    }

    public List<String> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<String> checkList) {
        this.checkList = checkList;
    }
}