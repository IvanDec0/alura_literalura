package com.literalura.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class HttpService {

    HttpClient client = HttpClient.newHttpClient();
    ObjectMapper mapper = new ObjectMapper();

    @Value("${gutendex.url}")
    private String url;

    public Map sendRequest()  {

        try {
            // Crear la solicitud a la API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            // Enviar la solicitud y obtener la respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Conertir la respuesta a un Map y devoverla
            return mapper.readValue(response.body(), Map.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error al enviar la solicitud: " + e.getMessage());
            return null;
        }
    }

    public Map sendRequest(String id) {

        try {
            // Crear la solicitud a la API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url+id+"/"))
                    .build();

            // Enviar la solicitud y obtener la respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Conertir la respuesta a un Map y devoverla
            return mapper.readValue(response.body(), Map.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error al enviar la solicitud: " + e.getMessage());
            return null;
        }
    }
}
