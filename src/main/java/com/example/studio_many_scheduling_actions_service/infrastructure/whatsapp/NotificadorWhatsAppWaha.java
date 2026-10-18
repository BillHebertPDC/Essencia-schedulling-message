package com.example.studio_many_scheduling_actions_service.infrastructure.whatsapp;

import com.example.studio_many_scheduling_actions_service.domain.model.MascaraTelefone;
import com.example.studio_many_scheduling_actions_service.domain.port.NotificadorWhatsApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Envia texto livre via WAHA (WhatsApp HTTP API). Numeros chegam validados pelo
 * caso de uso (nao ha telefone padrao de fallback).
 */
@Component
@ConditionalOnProperty(name = "whatsapp.provider", havingValue = "waha", matchIfMissing = true)
public class NotificadorWhatsAppWaha implements NotificadorWhatsApp {

    private static final Logger log = LoggerFactory.getLogger(NotificadorWhatsAppWaha.class);

    private static final String SUFIXO_CHAT_ID = "@c.us";

    private final RestClient restClient;
    private final String chaveApi;
    private final String sessao;

    public NotificadorWhatsAppWaha(RestClient.Builder construtorRestClient,
            @Value("${waha.api.url}") String urlApi,
            @Value("${waha.api.apikey:}") String chaveApi,
            @Value("${waha.session:Teste}") String sessao) {
        this.chaveApi = chaveApi;
        this.sessao = sessao;
        this.restClient = construtorRestClient
                .baseUrl(urlApi)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public void enviarTexto(String numero, String texto) {
        if (numero == null || numero.trim().isBlank()) {
            throw new IllegalArgumentException("Numero e obrigatorio");
        }

        String chatId = numero.trim() + SUFIXO_CHAT_ID;

        log.info("Enviando WhatsApp via WAHA: sessao={}, chatId={}", sessao, MascaraTelefone.mascarar(chatId));

        try {
            RestClient.RequestBodySpec requisicao = restClient.post().uri("/api/sendText");
            if (chaveApi != null && !chaveApi.isBlank()) {
                requisicao.header("X-Api-Key", chaveApi);
            }
            requisicao.body(Map.of("session", sessao, "chatId", chatId, "text", texto))
                    .retrieve()
                    .toBodilessEntity();
            log.info("WhatsApp enviado com sucesso: chatId={}", MascaraTelefone.mascarar(chatId));
        } catch (RestClientException ex) {
            log.error("Falha ao enviar WhatsApp via WAHA: chatId={}, erro={}",
                    MascaraTelefone.mascarar(chatId), ex.getMessage());
            throw new IllegalStateException("Falha no envio via WAHA", ex);
        }
    }
}
