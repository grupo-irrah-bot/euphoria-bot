package br.com.grupoirrah.euphoriabot.config;

import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ConfigurationProperties(prefix = "discord")
@Getter
@Setter
public class DiscordConfig {

    // Configurações gerais do Discord
    private String botToken;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    // IDs dos canais
    private String verificationChannelId;
    private String teamRoleChannelId;

    // Role após a verificação do e-mail
    private String roleIrradianteId;

    // String de mapeamento de categorias para cargos
    private String categoryRoleMapping;

    // IDs dos times
    private Map<String, String> teams = new HashMap<>();

    // IDs das funções
    private Map<String, String> roles = new HashMap<>();

    // Validação de email
    private String mailboxValidatorApiKey;

     // Converte o mapeamento de categorias para cargos em um Map
    public Map<String, String> getParsedCategoryRoleMapping() {
        Map<String, String> parsedMapping = new HashMap<>();
        if (categoryRoleMapping != null && !categoryRoleMapping.isBlank()) {
            String[] mappings = categoryRoleMapping.split(",");
            for (String mapping : mappings) {
                String[] parts = mapping.split(":");
                if (parts.length == 2) {
                    parsedMapping.put(parts[0], parts[1]);
                } else {
                    LogUtil.logError(log, "❌ Configuração inválida de mapeamento de categorias: {}",
                        mapping);
                }
            }
        }
        return parsedMapping;
    }

}
