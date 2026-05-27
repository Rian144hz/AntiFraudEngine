package org.antifraudengine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AntiFraudEngineApplication

fun main(args: Array<String>) {
    runApplication<AntiFraudEngineApplication>(*args)
}
