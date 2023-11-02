package com.mirea

import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.r2dbc.connection.init.*
import org.springframework.core.io.ClassPathResource

import io.r2dbc.spi.ConnectionFactory


@Configuration
public class DbSchemaInitOnStartup {

    @Bean
    ConnectionFactoryInitializer initializer(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        ResourceDatabasePopulator resource = new ResourceDatabasePopulator(new ClassPathResource("init.sql"))
        initializer.setDatabasePopulator(resource)
        return initializer
    }
}