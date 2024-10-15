package com.jsrdev.screenmatch.service

interface IConvertData {
    fun <T> getData(json: String, genericClass: Class<T>): T
}