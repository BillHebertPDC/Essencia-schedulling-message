package com.example.studio_many_scheduling_actions_service.application.service;

import com.example.studio_many_scheduling_actions_service.application.dto.NotificacaoWhatsApp;
import com.example.studio_many_scheduling_actions_service.domain.model.MascaraTelefone;
import com.example.studio_many_scheduling_actions_service.domain.port.NotificadorWhatsApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ServicoEnvioWhatsAppTest {

    @Mock
    private NotificadorWhatsApp notificadorWhatsApp;

    @InjectMocks
    private ServicoEnvioWhatsApp servico;

    @Test
    void deveRenderizarPlaceholdersComOsParametrosRecebidos() {
        servico.enviar(new NotificacaoWhatsApp(
                "5511999999999",
                "Ola {{nome}}, seu codigo e {{codigo}}.",
                Map.of("nome", "Ana", "codigo", "123456")));

        ArgumentCaptor<String> texto = ArgumentCaptor.forClass(String.class);
        verify(notificadorWhatsApp).enviarTexto(eq("5511999999999"), texto.capture());
        assertEquals("Ola Ana, seu codigo e 123456.", texto.getValue());
    }

    @Test
    void deveAceitarParametrosNaoUtilizados() {
        servico.enviar(new NotificacaoWhatsApp(
                "5511999999999",
                "Mensagem sem placeholders.",
                Map.of("ignorado", "valor")));

        verify(notificadorWhatsApp).enviarTexto("5511999999999", "Mensagem sem placeholders.");
    }

    @Test
    void deveTratarParametrosNulosComoVazios() {
        servico.enviar(new NotificacaoWhatsApp("5511999999999", "Mensagem sem placeholders.", null));

        verify(notificadorWhatsApp).enviarTexto("5511999999999", "Mensagem sem placeholders.");
    }

    @Test
    void deveRejeitarPlaceholderSemParametro() {
        assertThrows(IllegalArgumentException.class,
                () -> servico.enviar(new NotificacaoWhatsApp(
                        "5511999999999",
                        "Ola {{nome}}",
                        Map.of("outro", "valor"))));

        verifyNoInteractions(notificadorWhatsApp);
    }

    @Test
    void deveRejeitarNumeroEmBranco() {
        assertThrows(IllegalArgumentException.class,
                () -> servico.enviar(new NotificacaoWhatsApp("  ", "Ola", Map.of())));

        verifyNoInteractions(notificadorWhatsApp);
    }

    @Test
    void deveRejeitarMensagemEmBranco() {
        assertThrows(IllegalArgumentException.class,
                () -> servico.enviar(new NotificacaoWhatsApp("5511999999999", "  ", Map.of())));

        verifyNoInteractions(notificadorWhatsApp);
    }

    @Test
    void mascaraTelefoneDeveOcultarDigitosDoMeio() {
        assertEquals("55*******7845", MascaraTelefone.mascarar("5511944407845"));
        assertEquals("****", MascaraTelefone.mascarar("123"));
        assertEquals("****", MascaraTelefone.mascarar(null));
    }
}
