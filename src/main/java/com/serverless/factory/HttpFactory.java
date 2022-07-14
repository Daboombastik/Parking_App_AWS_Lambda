package com.serverless.factory;

import com.serverless.ApiGatewayResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;

public class HttpFactory {

    private static final String URL = "https://data.nantesmetropole.fr/api/records/1.0/search/?dataset=244400404_parkings-publics-nantes-disponibilites&q=&rows=-1&facet=grp_nom&facet=grp_statut";

    public URI createURI() {
        return URI.create(URL);
    }

    public HttpRequest createHttpRequest() {
        return HttpRequest.newBuilder()
                .uri(createURI())
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();
    }

    public HttpClient createClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public HttpResponse<String> sendRequestAndReceiveResponse() throws IOException, InterruptedException, ExecutionException {
        return this.createClient().send(this.createHttpRequest(), HttpResponse.BodyHandlers.ofString());
    }
}
