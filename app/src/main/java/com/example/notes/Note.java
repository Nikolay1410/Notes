package com.example.notes;

public class Note {
    private int id;
    private String title;
    private String description;
    private String data;
    private int priority;

    public Note(int id, String title, String description, String data, int priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.data = data;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getData() {
        return data;
    }

    public int getPriority() {
        return priority;
    }
}
