package com.serverless;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.dto.ParkingDTO;
import com.serverless.factory.HttpFactory;
import com.serverless.service.ParkingService;
import com.serverless.service.implementations.ParkingServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(Handler.class);
    private final ParkingService parkingService;
    private final ObjectMapper objectMapper;

    public Handler() {
        this.parkingService = new ParkingServiceImpl(new HttpFactory());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: {}", input);
        Response responseBody = null;
        List<ParkingDTO> parkingList = this.parkingService.getParkingList();
        if (!parkingList.isEmpty()) {
            String json = null;
            try {
                json = this.objectMapper.writeValueAsString(parkingList.stream().map(ParkingDTO::toString).collect(Collectors.joining(",")));
                return responseOK(json, input);
            } catch (JsonProcessingException e) {
                LOG.info("Exception message: {}", e.getMessage());
            }
        }
        return responseKO(input);
    }

    private ApiGatewayResponse responseOK(String json, Map<String, Object> input) {
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(new Response(json, input))
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                .build();

    }

    private ApiGatewayResponse responseKO(Map<String, Object> input) {
        return ApiGatewayResponse.builder()
                .setStatusCode(400)
                .setObjectBody(new Response("Api not available, try to connect later", input))
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                .build();
    }
}
