package com.example.studio_many_scheduling_actions_service.infrastructure.whatsapp;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;

class NotificadorWhatsAppTwilioTest {

    private NotificadorWhatsAppTwilio notificador;

    @BeforeEach
    void configurar() {
        notificador = new NotificadorWhatsAppTwilio("ACconta", "token", "", "whatsapp:+14155238886");
    }

    @Test
    void deveEnviarTextoComTelefoneNormalizadoParaE164() {
        try (MockedStatic<Message> controle = mockStatic(Message.class)) {
            controle.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenAnswer(invocacao -> {
                        PhoneNumber para = invocacao.getArgument(0);
                        PhoneNumber de = invocacao.getArgument(1);
                        String corpo = invocacao.getArgument(2);

                        assertEquals("whatsapp:+5511944407845", para.toString());
                        assertEquals("whatsapp:+14155238886", de.toString());
                        assertTrue(corpo.contains("codigo: 123456"));

                        return mock(MessageCreator.class);
                    });

            notificador.enviarTexto("5511944407845", "Seu codigo: 123456");

            controle.verify(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()));
        }
    }

    @Test
    void deveRejeitarNumeroEmBranco() {
        try (MockedStatic<Message> controle = mockStatic(Message.class)) {
            assertThrows(IllegalArgumentException.class,
                    () -> notificador.enviarTexto("  ", "Seu codigo: 123456"));

            controle.verifyNoInteractions();
        }
    }
}
