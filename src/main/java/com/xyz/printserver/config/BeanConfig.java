package com.xyz.printserver.config;

import com.xyz.printserver.model.DocIdContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public DocIdContainer createDocIdContainer() {
        return new DocIdContainer();
    }
}
