package com.example.studio_many_scheduling_actions_service.infrastructure.whatsapp;

import com.example.studio_many_scheduling_actions_service.domain.model.MascaraTelefone;
import com.example.studio_many_scheduling_actions_service.domain.port.NotificadorWhatsApp;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Envia texto livre via Twilio (WhatsApp Business API). Numeros chegam
 * validados pelo caso de uso; aqui sao normalizados para E.164.
 */
@Component
@ConditionalOnProperty(name = "whatsapp.provider", havingValue = "twilio", matchIfMissing = false)
public class NotificadorWhatsAppTwilio implements NotificadorWhatsApp {

    private static final Logger log = LoggerFactory.getLogger(NotificadorWhatsAppTwilio.class);

    private static final String PREFIXO_WHATSAPP = "whatsapp:";

    private final String accountSid;
    private final String authToken;
    private final String apiKeySid;
    private final String whatsappNumber;

    public NotificadorWhatsAppTwilio(
            @Value("${twilio.account-sid}") String accountSid,
            @Value("${twilio.auth-token}") String authToken,
            @Value("${twilio.api-key-sid:}") String apiKeySid,
            @Value("${twilio.whatsapp-number}") String whatsappNumber) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.apiKeySid = apiKeySid;
        this.whatsappNumber = whatsappNumber;
    }

    @PostConstruct
    public void inicializar() {
        if (apiKeySid != null && !apiKeySid.isBlank()) {
            Twilio.init(apiKeySid, authToken, accountSid);
        } else {
            Twilio.init(accountSid, authToken);
        }
    }

    @Override
    public void enviarTexto(String numero, String texto) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Numero e obrigatorio");
        }

        String telefoneE164 = TelefoneE164.normalizar(numero);

        log.info("Enviando WhatsApp via Twilio: para={}", MascaraTelefone.mascarar(telefoneE164));

        try {
            Message.creator(
                    new PhoneNumber(PREFIXO_WHATSAPP + telefoneE164),
                    new PhoneNumber(whatsappNumber),
                    texto).create();
            log.info("WhatsApp enviado com sucesso: para={}", MascaraTelefone.mascarar(telefoneE164));
        } catch (Exception ex) {
            log.error("Falha ao enviar WhatsApp via Twilio: para={}, erro={}",
                    MascaraTelefone.mascarar(telefoneE164), ex.getMessage());
            throw new IllegalStateException("Falha no envio via Twilio", ex);
        }
    }
}
