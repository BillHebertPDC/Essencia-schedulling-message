package com.example.studio_many_scheduling_actions_service.application.port;

import com.example.studio_many_scheduling_actions_service.application.dto.NotificacaoWhatsApp;

/**
 * Porta de entrada da aplicacao: envio generico de mensagem WhatsApp a partir
 * do comando consumido da fila. Consumida pelos adaptadores de entrada.
 */
public interface EnvioWhatsApp {

    void enviar(NotificacaoWhatsApp notificacao);
}
