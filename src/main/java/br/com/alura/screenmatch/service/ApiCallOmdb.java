package br.com.alura.screenmatch.service;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiCallOmdb {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String BASE_URL = "https://www.omdbapi.com/";
    private static final String API_KEY = dotenv.get("API_KEY");

    private final HttpClient client;

    public ApiCallOmdb(){
        this.client=HttpClient.newHttpClient();
    }

    public String get(Map<String, String> params) throws IOException, InterruptedException{
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        urlBuilder.append("?");

        params.forEach((key, value) -> {
            urlBuilder.append(key)
                    .append("=")
                    .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
                    .append("&");
        });

        urlBuilder.append("apikey=").append(API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBuilder.toString()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro na requisição: " + response.statusCode());
        }
        return response.body();
    }
}