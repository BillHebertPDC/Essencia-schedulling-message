package com.example.studio_many_scheduling_actions_service.infrastructure.rabbitmq;

import com.example.studio_many_scheduling_actions_service.application.dto.NotificacaoWhatsApp;
import com.example.studio_many_scheduling_actions_service.application.port.EnvioWhatsApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OuvinteNotificacaoWhatsAppTest {

    @Mock
    private EnvioWhatsApp envioWhatsApp;

    @InjectMocks
    private OuvinteNotificacaoWhatsApp ouvinte;

    @Test
    void deveDelegarNumeroEParametrosDaMensagemParaOCasoDeUso() {
        NotificacaoWhatsApp notificacao = new NotificacaoWhatsApp(
                "5511999999999",
                "Ola {{nome}}, seu codigo e {{codigo}}.",
                Map.of("nome", "Ana", "codigo", "123456"));

        ouvinte.receber(notificacao);

        verify(envioWhatsApp).enviar(same(notificacao));
    }

    @Test
    void devePropagarFalhaParaORabbitSemConsumirMensagem() {
        doThrow(new IllegalStateException("Falha no envio")).when(envioWhatsApp).enviar(any());

        assertThrows(IllegalStateException.class,
                () -> ouvinte.receber(new NotificacaoWhatsApp("5511999999999", "Ola", Map.of())));
    }
}
