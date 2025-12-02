package com.elienai.springfood.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.elienai.springfood.infrastructure.repository.CustomJpaRepositoryImpl;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.elienai.springfood.domain.repository",
    repositoryBaseClass = CustomJpaRepositoryImpl.class
)
public class JpaConfig {
}
