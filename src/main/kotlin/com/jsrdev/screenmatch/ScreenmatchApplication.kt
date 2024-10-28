package com.jsrdev.screenmatch

import com.jsrdev.screenmatch.main.MenuMain
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScreenmatchApplication: CommandLineRunner {
    override fun run(vararg args: String?) {
        MenuMain().showMenu()
    }
}

fun main(args: Array<String>) {
    runApplication<ScreenmatchApplication>(*args)
}
