package cl.bilix.scrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import cl.bilix.scrapper.helpers.Terminal;
import cl.bilix.scrapper.properties.Properties;

@SpringBootApplication
public class ScrapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrapperApplication.class, args);
	}

	@Bean
	@ConfigurationProperties(Terminal.STI)
	Properties stiProperties() {
		Properties properties = new Properties();
		properties.setMap(Terminal.STI);
		return properties;
	}

	@Bean
	@ConfigurationProperties(Terminal.PC)
	Properties pcProperties() {
		Properties properties = new Properties();
		properties.setMap(Terminal.PC);
		return properties;
	}

	@Bean
	@ConfigurationProperties(Terminal.SILOGPORT)
	Properties silogportProperties() {
		Properties properties = new Properties();
		properties.setMap(Terminal.SILOGPORT);
		return properties;
	}

	@Bean
	@ConfigurationProperties(Terminal.TPS)
	Properties tpsProperties() {
		Properties properties = new Properties();
		properties.setMap(Terminal.TPS);
		return properties;
	}

}
