package com.example.reseau_social.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.dtos.NotificationDTO;
import com.example.reseau_social.models.Notification;
import com.example.reseau_social.repositories.NotificationRepository;

import jakarta.transaction.Transactional;

// Service class for managing Notification entities
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

    public Notification updateNotification(String id, NotificationDTO dto) {
        return notificationRepository.findById(id).map(n -> {
            if (dto.getMessage() != null) {
                n.setContent(dto.getMessage());
            }
            if (dto.getLue() != null) {
                n.setRead(dto.getLue());
            }
            return notificationRepository.save(n);
        }).orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
    }

    public void markAllAsRead(Integer userId) {
        List<Notification> userNotifications = notificationRepository.findByUserId(userId);
        userNotifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(userNotifications);
    }

    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }

    // Mappers
    public Notification createNotificationFromDTO(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setUserId(dto.getUtilisateurId());
        notification.setType(dto.getType());
        notification.setContent(dto.getMessage());
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        return notification;
    }

    public NotificationDTO notificationToResponseDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUtilisateurId(notification.getUserId());
        dto.setType(notification.getType());
        dto.setMessage(notification.getContent());
        dto.setLue(notification.getRead());
        dto.setDateCreation(notification.getCreatedAt());
        return dto;
    }

    public List<NotificationDTO> notificationsToResponseDTOList(List<Notification> notifications) {
        return notifications.stream().map(this::notificationToResponseDTO).collect(Collectors.toList());
    }
}
