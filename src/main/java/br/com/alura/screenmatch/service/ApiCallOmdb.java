package br.com.alura.screenmatch.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ApiCallOmdb {

    private final String baseUrl;
    private final String apiKey;
    private final HttpClient client;

    public ApiCallOmdb(
            @Value("${omdb.base-url}") String baseUrl,
            @Value("${omdb.api-key}") String apiKey
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.client = HttpClient.newHttpClient();
    }

    public String get(Map<String, String> params) throws IOException, InterruptedException {
        StringBuilder urlBuilder = new StringBuilder(baseUrl).append("?");

        params.forEach((key, value) -> urlBuilder.append(key)
                .append("=")
                .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
                .append("&"));

        urlBuilder.append("apikey=").append(apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBuilder.toString()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro na requisição OMDb: " + response.statusCode());
        }
        return response.body();
    }
}