package com.jsrdev.screenmatch.main

class ExampleStreams {
    fun main() {

        val names = listOf("Brenda", "Luis", "Ruben", "Eric", "Maria Fernanda", "Genesys", "Byron")

        // Stream sirve para realizar varias operaciones encadenadas y no cambia la coleccion original
        names.stream()
            .sorted()
            .filter { it.startsWith("B", ignoreCase = true) }
            .map { it.uppercase() }
            .limit(4)
            .forEach(::println)

        println()

        names.asSequence() // convirtiendo la lista para una secuencia
            .sorted()
            .filter { it.startsWith("B", ignoreCase = true) }
            .map { it.uppercase() }
            .take(4)
            .forEach(::println)


        ///
        val numbers = listOf(1, 2, 3, 4, 5)

        // Sin sequence: cada operación crea una lista intermedia
        val resultado = numbers
            .map { it * 2 }    // Crea una lista intermedia con [2, 4, 6, 8, 10]
            .filter { it > 5 }  // Crea otra lista intermedia con [6, 8, 10]

        //println(resultado)  // Output: [6, 8, 10]

        // Usando sequence: las operaciones no crean listas intermedias
        val result = numbers.asSequence()
            .map { it * 2 }    // No crea una lista intermedia aún
            .filter { it > 5 } // No crea una lista intermedia aún
            .toList()          // Aquí se ejecutan todas las operaciones

        //println(result)  // Output: [6, 8, 10]


        //
        // Generamos una secuencia infinita de números impares
        val oddNumbers = generateSequence(1) { it + 2 }

        // Tomamos los primeros 5 números impares
        val firstFiveOddNumbers = oddNumbers.take(5).toList()

        //println(firstFiveOddNumbers)  // Output: [1, 3, 5, 7, 9]

    }
}