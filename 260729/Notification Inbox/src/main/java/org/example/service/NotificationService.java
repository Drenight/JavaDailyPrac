package org.example.service;

import org.example.dto.NoticiationStatus;
import org.example.dto.Notification;

import java.util.*;

import static java.util.Collections.reverse;

public class NotificationService {
    private final Integer maxMessageNumber;
    private final HashMap<String, LinkedHashMap<String, Notification>> User2Notifications;

    public NotificationService(Integer maxMessageNumber) {
        this.maxMessageNumber = maxMessageNumber;
        User2Notifications = new HashMap<>();
    }

    public Boolean deliver(String userId, String notificationId, String message) {
        Notification notification = new Notification(message, NoticiationStatus.UNREAD);

        if(!User2Notifications.containsKey(userId)){
            User2Notifications.put(userId, new LinkedHashMap<>(maxMessageNumber, 0.75f, true){

                @Override
                protected boolean removeEldestEntry(Map.Entry eldest) {
                     return size() > maxMessageNumber;
                }
            });
        }

        LinkedHashMap<String, Notification> notificationMap = User2Notifications.get(userId);
        if(notificationMap.containsKey(notificationId)){
            if(message.equals(notificationMap.get(notificationId).message())){
                throw new RuntimeException("Duplicate notification.");
            }else{
                throw new RuntimeException("Same notification id but different message.");
            }
        }else{
            notificationMap.put(notificationId, notification);
        }
        return true;
    }

    public ArrayList<Notification> getNotifications(String userId, Integer limit){
        if(limit<0){
            throw new RuntimeException("Invalid limit");
        }

        if(!User2Notifications.containsKey(userId)){
            return new ArrayList<>(){};
        }
        LinkedHashMap<String, Notification> notificationMap = User2Notifications.get(userId);

        ArrayList<Notification> notifications = new ArrayList<>();
        notificationMap.forEach((k,v) -> notifications.add(v));
        reverse(notifications);

        try{
            return new ArrayList(notifications.subList(0, Math.min(limit, notifications.size())));
        } catch(Exception e) {
            throw new RuntimeException(String.format("Casting error, original error: %s", e.toString()), e);
        }
    }

    public Boolean markAsRead(String userId, String notificationId){
        if(!User2Notifications.containsKey(userId)){
            throw new RuntimeException("Non-existed user.");
        }
        LinkedHashMap<String, Notification> notificationMap = User2Notifications.get(userId);
        if(!notificationMap.containsKey(notificationId)){
            throw new RuntimeException("Non-existed notification.");
        }

        Notification notification = notificationMap.get(notificationId);
        if(notification.noticiationStatus().equals(NoticiationStatus.READ)){
            throw new RuntimeException("Notification already read.");
        }else{
            notificationMap.put(notificationId, new Notification(notification.message(), NoticiationStatus.READ));
        }
        return true;
    }
}
