package org.example.dto;

public enum NoticiationStatus {
    READ("READ"),
    UNREAD("UNREAD");
    private final String value;

    NoticiationStatus(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
