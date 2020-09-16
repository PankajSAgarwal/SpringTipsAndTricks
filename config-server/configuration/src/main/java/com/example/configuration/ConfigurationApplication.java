package com.example.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

@Log4j2
@EnableConfigurationProperties(CustomProperties.class)
@SpringBootApplication
public class ConfigurationApplication {

	public static void main(String[] args) {
		//SpringApplication.run(ConfigurationApplication.class, args);
		new SpringApplicationBuilder()
				.sources(ConfigurationApplication.class)
				.initializers(applicationContext -> {
					final MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
					propertySources.addLast(new CustomPropertySource());
				})
				.run(args);
	}

	@Bean
	@RefreshScope
	ApplicationRunner applicationRunner(Environment environment,
										@Value("${HOME}")String userHome,
										@Value("${spring.datasource.url}") String springDataSourceUrl,
										@Value("${message-from-program-args:}") String messageFromProgramArgs,
										@Value("${greetings-message:Default Hello:${message-from-application-properties}}")String defaultValue,
										@Value("${customproperty-message}") String customMessage,
										@Value("${message-from-config-server}") String valueFromConfigServer,
										CustomProperties customProperties)
	{

		return args -> {
			log.info("message from application.properties " + environment.getProperty("message-from-application-properties"));
			log.info("default value from application.properties "+defaultValue);
			log.info("user home from the environment variables: "+userHome);
			log.info("message from program args: "+ messageFromProgramArgs);
			log.info("spring.datasource.url: "+springDataSourceUrl);
			log.info("message from custom PropertySource: " + customMessage);
			log.info("message from @ConfigurationProperties:"+customProperties.getMessage());
			log.info("message from spring cloud config server: " + valueFromConfigServer);

		};
	}

	static class CustomPropertySource extends PropertySource<String>{

		CustomPropertySource(){
			super("customproperty");
		}

		@Override
		public Object getProperty(String name) {
			if(name.equalsIgnoreCase("customproperty-message")){
				return "Hello from " + CustomPropertySource.class.getSimpleName() + "!";
			}
			return null;
		}
	}
}
@Data
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("custom1")
class CustomProperties{
	private final String message;
}