package org.spring.cloud.k8s.concertsservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s1p")
public class ConcertsConfiguration {

    private Boolean decorateConcerts;


    public Boolean getDecorateConcerts() {
        return decorateConcerts;
    }

    public void setDecorateConcerts(Boolean decorateConcerts) {
        this.decorateConcerts = decorateConcerts;
    }
}
