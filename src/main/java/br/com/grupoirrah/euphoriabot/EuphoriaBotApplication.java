package br.com.grupoirrah.euphoriabot;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EuphoriaBotApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();

		// Configurações gerais do Discord
		setSystemPropertyIfNotNull("DISCORD_BOT_TOKEN", dotenv.get("DISCORD_BOT_TOKEN"));
		setSystemPropertyIfNotNull("DISCORD_CLIENT_ID", dotenv.get("DISCORD_CLIENT_ID"));
		setSystemPropertyIfNotNull("DISCORD_CLIENT_SECRET", dotenv.get("DISCORD_CLIENT_SECRET"));
		setSystemPropertyIfNotNull("DISCORD_REDIRECT_URI", dotenv.get("DISCORD_REDIRECT_URI"));

		// IDs dos canais
		setSystemPropertyIfNotNull("DISCORD_VERIFICATION_CHANNEL_ID", dotenv.get("DISCORD_VERIFICATION_CHANNEL_ID"));
		setSystemPropertyIfNotNull("DISCORD_TEAM_ROLE_CHANNEL_ID", dotenv.get("DISCORD_TEAM_ROLE_CHANNEL_ID"));

		// Role atribuída após verificação do e-mail
		setSystemPropertyIfNotNull("DISCORD_ROLE_IRRADIANTE_ID", dotenv.get("DISCORD_ROLE_IRRADIANTE_ID"));

		// Configuração de mapeamento de categorias para cargos
		setSystemPropertyIfNotNull("DISCORD_CATEGORY_ROLE_MAPPING", dotenv.get("DISCORD_CATEGORY_ROLE_MAPPING"));

		// IDs dos times
		setSystemPropertyIfNotNull("DISCORD_TEAM_Z_API_ID", dotenv.get("DISCORD_TEAM_Z_API_ID"));
		setSystemPropertyIfNotNull("DISCORD_TEAM_KIGI_ID", dotenv.get("DISCORD_TEAM_KIGI_ID"));
		setSystemPropertyIfNotNull("DISCORD_TEAM_DISPARA_AI_ID", dotenv.get("DISCORD_TEAM_DISPARA_AI_ID"));
		setSystemPropertyIfNotNull("DISCORD_TEAM_PLUG_CHAT_ID", dotenv.get("DISCORD_TEAM_PLUG_CHAT_ID"));
		setSystemPropertyIfNotNull("DISCORD_TEAM_GPT_MARKER_ID", dotenv.get("DISCORD_TEAM_GPT_MARKER_ID"));
		setSystemPropertyIfNotNull("DISCORD_TEAM_MARKETING_ID", dotenv.get("DISCORD_TEAM_MARKETING_ID"));
		setSystemPropertyIfNotNull("DISCORD_TEAM_FINANCEIRO_ID", dotenv.get("DISCORD_TEAM_FINANCEIRO_ID"));
		setSystemPropertyIfNotNull("DISCORD_TEAM_RH_PEOPLE_ID", dotenv.get("DISCORD_TEAM_RH_PEOPLE_ID"));

		// IDs das funções
		setSystemPropertyIfNotNull("DISCORD_ROLE_CUSTOMER_EXPERIENCE_ID",
			dotenv.get("DISCORD_ROLE_CUSTOMER_EXPERIENCE_ID"));

		setSystemPropertyIfNotNull("DISCORD_ROLE_PRODUCT_ID", dotenv.get("DISCORD_ROLE_PRODUCT_ID"));
		setSystemPropertyIfNotNull("DISCORD_ROLE_DEVELOPER_ID", dotenv.get("DISCORD_ROLE_DEVELOPER_ID"));
		setSystemPropertyIfNotNull("DISCORD_ROLE_SUPPORT_ID", dotenv.get("DISCORD_ROLE_SUPPORT_ID"));

		// Mailbox Validator API Key
		setSystemPropertyIfNotNull("MAILBOXVALIDATOR_API_KEY", dotenv.get("MAILBOXVALIDATOR_API_KEY"));

		// Adiciona todas as variáveis do .env como propriedades do sistema
		dotenv.entries().forEach(entry -> setSystemPropertyIfNotNull(entry.getKey(), entry.getValue()));

		SpringApplication.run(EuphoriaBotApplication.class, args);
	}

	private static void setSystemPropertyIfNotNull(String key, String value) {
		if (value != null) {
			System.setProperty(key, value);
		}
	}

}
