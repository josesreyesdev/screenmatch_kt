package com.jsrdev.screenmatch.service

import com.jsrdev.screenmatch.utils.Constants
import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.completion.CompletionResult
import com.theokanning.openai.service.OpenAiService

class OpenAIService {

    companion object {
        fun getTranslation(text: String): String {
            val service = OpenAiService(Constants.API_KEY_OPENAI)

            val requisition: CompletionRequest = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt("traduce a español el siguiente texto: $text")
                .maxTokens(1000)
                .temperature(0.7)
                .build()

            val response: CompletionResult = service.createCompletion(requisition)

            return response.choices[0].text
        }
    }
}