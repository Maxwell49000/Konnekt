package com.example.reseau_social.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.reseau_social.models.Notification;
import com.example.reseau_social.repositories.NotificationRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void createNotification_savesAndReturns() {
        Notification n = new Notification();
        n.setUserId(1);
        n.setType("test");
        n.setContent("message");
        n.setCreatedAt(Instant.now());

        when(notificationRepository.save(n)).thenReturn(n);

        Notification result = notificationService.createNotification(n);

        assertNotNull(result);
        assertEquals("test", result.getType());
        verify(notificationRepository, times(1)).save(n);
    }

    @Test
    public void markAllAsRead_setsReadTrueAndSavesAll() {
        Notification n1 = new Notification();
        n1.setId("a");
        n1.setUserId(2);
        n1.setRead(false);

        Notification n2 = new Notification();
        n2.setId("b");
        n2.setUserId(2);
        n2.setRead(false);

        List<Notification> list = new ArrayList<>();
        list.add(n1);
        list.add(n2);

        when(notificationRepository.findByUserId(2)).thenReturn(list);

        notificationService.markAllAsRead(2);

        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationRepository, times(1)).saveAll(captor.capture());

        List<Notification> saved = captor.getValue();
        assertTrue(saved.stream().allMatch(n -> Boolean.TRUE.equals(n.getRead())));
    }
}
