package br.com.unopay.api;

import br.com.unopay.bootcommons.CommonsAutoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication(scanBasePackageClasses = {UnopayApiApplication.class, CommonsAutoConfig.class})
@EnableGlobalMethodSecurity(securedEnabled = true, proxyTargetClass = true, prePostEnabled = true)
public class UnopayApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnopayApiApplication.class, args);
	}
}
