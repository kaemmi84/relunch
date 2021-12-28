package net.kaemmi.relunchauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RelunchAuthApplication

fun main(args: Array<String>) {
	runApplication<RelunchAuthApplication>(*args)
}
