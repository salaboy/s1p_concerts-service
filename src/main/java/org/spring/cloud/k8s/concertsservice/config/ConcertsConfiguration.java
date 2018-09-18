package org.spring.cloud.k8s.concertsservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s1p")
public class ConcertsConfiguration {

    private Boolean decorate;


    public Boolean getDecorate() {
        return decorate;
    }

    public void setDecorate(Boolean decorate) {
        this.decorate = decorate;
    }
}
