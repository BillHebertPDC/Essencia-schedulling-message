package com.example.studio_many_scheduling_actions_service.infrastructure.whatsapp;

import com.example.studio_many_scheduling_actions_service.domain.model.MascaraTelefone;
import com.example.studio_many_scheduling_actions_service.domain.port.NotificadorWhatsApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Implementacao de teste: apenas registra o envio, sem chamar provedores.
 */
@Component
@ConditionalOnProperty(name = "whatsapp.provider", havingValue = "noop", matchIfMissing = false)
public class NotificadorWhatsAppNoOp implements NotificadorWhatsApp {

    private static final Logger log = LoggerFactory.getLogger(NotificadorWhatsAppNoOp.class);

    @Override
    public void enviarTexto(String numero, String texto) {
        log.info("NoOp: texto pronto para envio - numero={}, texto={}",
                MascaraTelefone.mascarar(numero), texto);
    }
}
