package com.example.reseau_social.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.models.Notification;
import com.example.reseau_social.repositories.NotificationRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(Notification n) {
        return notificationRepository.save(n);
    }

    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> getById(String id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> findByUser(Integer userId) {
        return notificationRepository.findByUserId(userId);
    }

    public Notification updateNotification(String id, Notification details) {
        return notificationRepository.findById(id).map(n -> {
            n.setContent(details.getContent());
            n.setRead(details.getRead());
            return notificationRepository.save(n);
        }).orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
    }

    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }
}
