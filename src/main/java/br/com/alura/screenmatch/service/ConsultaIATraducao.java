package br.com.alura.screenmatch.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ConsultaIATraducao {

    private final String baseUrl;
    private final String apiKey;
    private final HttpClient client;

    public ConsultaIATraducao(
            @Value("${google.translate.base-url}") String baseUrl,
            @Value("${google.translate.api-key}") String apiKey
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.client = HttpClient.newHttpClient();
    }

    public String traduzir(String texto) throws IOException, InterruptedException {
        String json = """
                {
                  "q": "%s",
                  "target": "pt"
                }
                """.formatted(texto.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro na tradução: " + response.statusCode());
        }
        return response.body();
    }
}