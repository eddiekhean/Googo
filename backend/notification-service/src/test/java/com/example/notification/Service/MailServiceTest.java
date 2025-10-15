package com.example.notification.Service;

import com.example.notification.factory.MailStrategyFactory;
import com.example.notification.model.MailType;
import com.example.notification.service.MailService;
import com.example.notification.strategy.MailStrategy;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.mockito.Mockito.*;

class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailStrategyFactory factory;

    @Mock
    private MailStrategy mockStrategy;

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void send_ShouldUseCorrectStrategyAndSendMail() throws Exception {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        when(factory.getStrategy(MailType.WELCOME)).thenReturn(mockStrategy);

        // When
        mailService.send("user@example.com", MailType.WELCOME, "mockData");

        // Then
        verify(factory).getStrategy(MailType.WELCOME);
        verify(mockStrategy).prepareMail(any(MimeMessageHelper.class), eq("mockData"));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void send_ShouldThrowRuntimeException_WhenStrategyFails() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(factory.getStrategy(MailType.WELCOME)).thenReturn(mockStrategy);

        doThrow(new RuntimeException("mock fail"))
                .when(mockStrategy)
                .prepareMail(any(), any());

        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> mailService.send("user@example.com", MailType.WELCOME, "data")
        );
    }
}