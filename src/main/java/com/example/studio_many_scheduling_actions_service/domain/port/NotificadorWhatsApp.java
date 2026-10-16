package com.example.studio_many_scheduling_actions_service.domain.port;

/**
 * Porta de saida do dominio: envio de mensagem de WhatsApp.
 * As implementacoes propagam falha de entrega como excecao (o listener decide
 * se reenfileira).
 */
public interface NotificadorWhatsApp {

    /**
     * Envia um texto livre para um numero.
     */
    void enviarTexto(String numero, String texto);
}
