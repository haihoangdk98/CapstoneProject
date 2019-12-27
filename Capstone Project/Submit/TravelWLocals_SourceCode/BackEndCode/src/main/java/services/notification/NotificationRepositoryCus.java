package services.notification;

import entities.Notification;

import java.util.List;

public interface NotificationRepositoryCus {
    public void save(Notification notification);
    public List<Notification> get(String receiver, int firstElement, int lastElement);
    public void updateSeen(String user);
}
