package com.example.maxlab3.model;

public class TextNote extends Note {
    private String content;

    public TextNote(String title, String content) {
        super(title);
        this.content = content;
    }

    @Override
    public String getSummary() {
        if (content == null || content.isEmpty()) {
            return getTitle() + ": (Empty)";
        }
        String preview = content.length() > 20 ? content.substring(0, 20) + "..." : content;
        return getTitle() + ": " + preview;
    }

    @Override
    public String getDetail() {
        return (content == null || content.isEmpty()) ? "(ไม่มีเนื้อหา)" : content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
