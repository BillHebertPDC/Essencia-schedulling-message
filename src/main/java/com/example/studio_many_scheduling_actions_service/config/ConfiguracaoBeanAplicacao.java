package com.example.studio_many_scheduling_actions_service.config;

import com.example.studio_many_scheduling_actions_service.application.port.EnvioWhatsApp;
import com.example.studio_many_scheduling_actions_service.application.service.ServicoEnvioWhatsApp;
import com.example.studio_many_scheduling_actions_service.domain.port.NotificadorWhatsApp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ponto de composicao da camada de aplicacao. O servico e uma classe pura
 * (sem anotacoes Spring) e e registrado aqui.
 */
@Configuration
public class ConfiguracaoBeanAplicacao {

    @Bean
    public EnvioWhatsApp envioWhatsApp(NotificadorWhatsApp notificadorWhatsApp) {
        return new ServicoEnvioWhatsApp(notificadorWhatsApp);
    }
}
