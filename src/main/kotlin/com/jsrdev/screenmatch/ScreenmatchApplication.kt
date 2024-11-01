package com.jsrdev.screenmatch

import com.jsrdev.screenmatch.main.MenuMain
import com.jsrdev.screenmatch.repository.SeriesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScreenmatchApplication @Autowired constructor(private val repository: SeriesRepository): CommandLineRunner {

    override fun run(vararg args: String?) {
        MenuMain(seriesRepository = repository).showMenu()
    }
}

fun main(args: Array<String>) {
    runApplication<ScreenmatchApplication>(*args)
}
