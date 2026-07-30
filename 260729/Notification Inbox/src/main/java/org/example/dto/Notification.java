package org.example.dto;

public record Notification (String message,
                            NoticiationStatus noticiationStatus,
                            String notificationId){
}
