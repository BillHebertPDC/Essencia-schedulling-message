package com.example.studio_many_scheduling_actions_service.application.service;

import com.example.studio_many_scheduling_actions_service.application.dto.NotificacaoWhatsApp;
import com.example.studio_many_scheduling_actions_service.application.port.EnvioWhatsApp;
import com.example.studio_many_scheduling_actions_service.domain.model.MascaraTelefone;
import com.example.studio_many_scheduling_actions_service.domain.port.NotificadorWhatsApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Caso de uso de envio generico de WhatsApp: valida a mensagem consumida da
 * fila, substitui placeholders {{chave}} pelos parametros recebidos e delega a
 * saida ao notificador. Responsabilidade unica: validacao + renderizacao.
 */
public class ServicoEnvioWhatsApp implements EnvioWhatsApp {

    private static final Logger log = LoggerFactory.getLogger(ServicoEnvioWhatsApp.class);

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([\\w.-]+)\\s*}}");

    private final NotificadorWhatsApp notificadorWhatsApp;

    public ServicoEnvioWhatsApp(NotificadorWhatsApp notificadorWhatsApp) {
        this.notificadorWhatsApp = notificadorWhatsApp;
    }

    @Override
    public void enviar(NotificacaoWhatsApp notificacao) {
        validar(notificacao);

        Map<String, String> parametros = notificacao.parametros() == null
                ? Map.of()
                : notificacao.parametros();

        String texto = renderizar(notificacao.mensagem(), parametros);

        log.info("Enviando WhatsApp: numero={}, chaves={}",
                MascaraTelefone.mascarar(notificacao.numero()),
                parametros.keySet());

        notificadorWhatsApp.enviarTexto(notificacao.numero(), texto);
    }

    String renderizar(String mensagem, Map<String, String> parametros) {
        Matcher ocorrencias = PLACEHOLDER.matcher(mensagem);
        StringBuilder texto = new StringBuilder();

        while (ocorrencias.find()) {
            String chave = ocorrencias.group(1);
            String valor = parametros.get(chave);
            if (valor == null) {
                throw new IllegalArgumentException("Parametro ausente para o placeholder: " + chave);
            }
            ocorrencias.appendReplacement(texto, Matcher.quoteReplacement(valor));
        }
        ocorrencias.appendTail(texto);
        return texto.toString();
    }

    private void validar(NotificacaoWhatsApp notificacao) {
        if (notificacao == null) {
            throw new IllegalArgumentException("Notificacao nao pode ser nula");
        }
        if (notificacao.numero() == null || notificacao.numero().isBlank()) {
            throw new IllegalArgumentException("Numero e obrigatorio");
        }
        if (notificacao.mensagem() == null || notificacao.mensagem().isBlank()) {
            throw new IllegalArgumentException("Mensagem e obrigatoria");
        }
    }
}
