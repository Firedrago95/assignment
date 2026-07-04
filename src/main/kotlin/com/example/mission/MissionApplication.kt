package com.example.mission

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class MissionApplication


fun main(args: Array<String>) {
	runApplication<MissionApplication>(*args)
}
