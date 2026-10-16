package com.example.studio_many_scheduling_actions_service.infrastructure.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia de envio generico de WhatsApp. Contrato: payload JSON
 * {@code {"numero": "...", "mensagem": "Ola {{nome}}...", "parametros": {...}}}
 * publicado no {@code exchange_notificacao} com routing key
 * {@code notificacao.whatsapp}.
 *
 * A fila possui DLQ: mensagens rejeitadas (payload invalido ou falha de envio
 * apos as tentativas) vao para {@code fila_notificacao_whatsapp_dlq}.
 */
@Configuration
public class ConfiguracaoNotificacaoRabbit {

    public static final String EXCHANGE_NOTIFICACAO = "exchange_notificacao";
    public static final String FILA_NOTIFICACAO_WHATSAPP = "fila_notificacao_whatsapp";
    public static final String ROUTING_KEY_NOTIFICACAO = "notificacao.whatsapp";

    public static final String EXCHANGE_NOTIFICACAO_DLQ = "exchange_notificacao_dlq";
    public static final String FILA_NOTIFICACAO_WHATSAPP_DLQ = "fila_notificacao_whatsapp_dlq";

    @Bean
    public DirectExchange exchangeNotificacao() {
        return new DirectExchange(EXCHANGE_NOTIFICACAO, true, false);
    }

    @Bean
    public DirectExchange exchangeNotificacaoDlq() {
        return new DirectExchange(EXCHANGE_NOTIFICACAO_DLQ, true, false);
    }

    @Bean
    public Queue filaNotificacaoWhatsApp() {
        return QueueBuilder.durable(FILA_NOTIFICACAO_WHATSAPP)
                .deadLetterExchange(EXCHANGE_NOTIFICACAO_DLQ)
                .deadLetterRoutingKey(ROUTING_KEY_NOTIFICACAO)
                .build();
    }

    @Bean
    public Queue filaNotificacaoWhatsAppDlq() {
        return QueueBuilder.durable(FILA_NOTIFICACAO_WHATSAPP_DLQ).build();
    }

    @Bean
    public Binding bindingNotificacaoWhatsApp(Queue filaNotificacaoWhatsApp,
            DirectExchange exchangeNotificacao) {
        return BindingBuilder.bind(filaNotificacaoWhatsApp)
                .to(exchangeNotificacao)
                .with(ROUTING_KEY_NOTIFICACAO);
    }

    @Bean
    public Binding bindingNotificacaoWhatsAppDlq(Queue filaNotificacaoWhatsAppDlq,
            DirectExchange exchangeNotificacaoDlq) {
        return BindingBuilder.bind(filaNotificacaoWhatsAppDlq)
                .to(exchangeNotificacaoDlq)
                .with(ROUTING_KEY_NOTIFICACAO);
    }

    @Bean
    public JacksonJsonMessageConverter jsonConverter() {
        return new JacksonJsonMessageConverter();
    }
}
