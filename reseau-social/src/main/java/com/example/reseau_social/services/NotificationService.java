package com.example.reseau_social.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.dtos.CreateNotificationDTO;
import com.example.reseau_social.dtos.NotificationResponseDTO;
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

    // Mappers
    public Notification createNotificationFromDTO(CreateNotificationDTO dto) {
        Notification notification = new Notification();
        notification.setUserId(dto.getUtilisateurId());
        notification.setType(dto.getType());
        notification.setContent(dto.getMessage());
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        return notification;
    }

    public NotificationResponseDTO notificationToResponseDTO(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getContent(),
                notification.getRead(),
                notification.getCreatedAt()
        );
    }

    public List<NotificationResponseDTO> notificationsToResponseDTOList(List<Notification> notifications) {
        return notifications.stream().map(this::notificationToResponseDTO).collect(Collectors.toList());
    }
}
