package net.kaemmi.relunchClient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RelunchClientApplication

fun main(args: Array<String>) {
	runApplication<RelunchClientApplication>(*args)
}
