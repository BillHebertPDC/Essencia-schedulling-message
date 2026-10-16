package com.example.studio_many_scheduling_actions_service.infrastructure.whatsapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class NotificadorWhatsAppWahaTest {

    private MockRestServiceServer servidor;
    private NotificadorWhatsAppWaha notificador;

    @BeforeEach
    void configurar() {
        RestClient.Builder construtor = RestClient.builder();
        servidor = MockRestServiceServer.bindTo(construtor).build();

        notificador = new NotificadorWhatsAppWaha(
                construtor,
                "http://localhost:3000",
                "minha-chave-api",
                "padrao");
    }

    @Test
    void deveEnviarTextoParaApiDoWaha() {
        servidor.expect(requestTo("http://localhost:3000/api/sendText"))
                .andExpect(method(POST))
                .andExpect(header("X-Api-Key", "minha-chave-api"))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess());

        notificador.enviarTexto("5511944407845", "Seu codigo: 123456");

        servidor.verify();
    }

    @Test
    void devePropagarFalhaDeEnvioParaORabbit() {
        servidor.expect(requestTo("http://localhost:3000/api/sendText"))
                .andExpect(method(POST))
                .andRespond(withServerError());

        IllegalStateException excecao = assertThrows(IllegalStateException.class,
                () -> notificador.enviarTexto("5511944407845", "Seu codigo: 123456"));

        assertEquals("Falha no envio via WAHA", excecao.getMessage());
        servidor.verify();
    }
}
