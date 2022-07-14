package com.serverless.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.serverless.factory.HttpFactory;
import com.serverless.model.DataSet;
import com.serverless.model.ParkingRecords;
import com.serverless.dto.ParkingDTO;
import com.serverless.service.ParkingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ParkingServiceImpl implements ParkingService {

    private static final Logger LOG = LogManager.getLogger(ParkingServiceImpl.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final HttpFactory httpFactory;

    public ParkingServiceImpl(HttpFactory httpFactory) {
        this.httpFactory = httpFactory;
    }

    @Override
    public List<ParkingDTO> getParkingList() {
        List<ParkingDTO> parkingList = new ArrayList<>();
        String httpResponse = this.receiveHttpResponse();
        if (httpResponse != null && !httpResponse.isEmpty()) {
            ParkingRecords records = this.getParkingRecords(httpResponse);
            parkingList = this.mapAndCollect(records);
        }
        return parkingList;
    }


    private String receiveHttpResponse() {
        try {
            return httpFactory.sendRequestAndReceiveResponse().body();
        } catch (IOException | InterruptedException | ExecutionException e) {
            String error = getExceptionMsg(e);
            LOG.info("Exception message: {}", error);
            return null;
        }
    }

    private ParkingRecords getParkingRecords(String httpResponse) {
        ParkingRecords parkingRecords = new ParkingRecords();
        try {
            parkingRecords = mapper.readValue(httpResponse, ParkingRecords.class);
        } catch (Exception e) {
            String error = getExceptionMsg(e);
            LOG.info("Exception message: {}", error);
        }
        return parkingRecords;
    }

    private List<ParkingDTO> mapAndCollect(ParkingRecords records) {
        return records.getRecords().stream()
                .map(dataSet -> CompletableFuture.supplyAsync(() -> mapToDto(dataSet)))
                .map(tasks -> tasks.join())
                .collect(Collectors.toList());
    }

    private ParkingDTO mapToDto(DataSet dataSet) {
        ParkingDTO parkingDTO = new ParkingDTO();
        parkingDTO.setNom(dataSet.getFields().getGrpNom());
        parkingDTO.setStatut(defineParkingStatus(dataSet.getFields().getGrpStatut()));
        parkingDTO.setNbPlacesDispo(dataSet.getFields().getGrpDisponible());
        parkingDTO.setNbPlacesTotal(dataSet.getFields().getGrpExploitation());
        parkingDTO.setHeureMaj(dataSet.getFields().getGrpHorodatage());
        return parkingDTO;
    }

    private String defineParkingStatus(String code) {
        String result;
        switch (code) {
            case "1":
                result = "FERME";
                break;
            case "2":
                result = "ABONNES";
                break;
            case "5":
                result = "OUVERT";
                break;
            default:
                result = "Données non disponibles";
        }
        return result;
    }

    private String getExceptionMsg(Throwable e) {
        return e.getCause() != null ? e.getCause().getMessage() : e.initCause(e).getMessage();
//        Throwable cause = null;
//        Throwable result = e;
//        while (null != (cause = result.getCause()) && (result != cause)) {
//            result = cause;
//        }
//        return result;
    }
}
