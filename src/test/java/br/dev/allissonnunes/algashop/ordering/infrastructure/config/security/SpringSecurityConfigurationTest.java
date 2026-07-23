package br.dev.allissonnunes.algashop.ordering.infrastructure.config.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@Import(SpringSecurityConfiguration.class)
@TestConfiguration(proxyBeanMethods = false)
public class SpringSecurityConfigurationTest {

}