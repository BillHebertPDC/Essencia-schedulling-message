package com.example.studio_many_scheduling_actions_service.infrastructure.rabbitmq;

import com.example.studio_many_scheduling_actions_service.application.dto.NotificacaoWhatsApp;
import com.example.studio_many_scheduling_actions_service.application.port.EnvioWhatsApp;
import com.example.studio_many_scheduling_actions_service.domain.model.MascaraTelefone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada: le o numero e os parametros do comando publicado na
 * fila, valida e delega o envio ao caso de uso. Nao contem regra de negocio.
 * Erros validacao/entrega sobem para o container: retry limitado e DLQ.
 */
@Component
public class OuvinteNotificacaoWhatsApp {

    private static final Logger log = LoggerFactory.getLogger(OuvinteNotificacaoWhatsApp.class);

    private final EnvioWhatsApp envioWhatsApp;

    public OuvinteNotificacaoWhatsApp(EnvioWhatsApp envioWhatsApp) {
        this.envioWhatsApp = envioWhatsApp;
    }

    @RabbitListener(queues = ConfiguracaoNotificacaoRabbit.FILA_NOTIFICACAO_WHATSAPP,
            messageConverter = "jsonConverter")
    public void receber(NotificacaoWhatsApp notificacao) {
        log.info("Notificacao recebida da fila: numero={}",
                MascaraTelefone.mascarar(notificacao == null ? null : notificacao.numero()));
        envioWhatsApp.enviar(notificacao);
    }
}
