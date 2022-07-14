package com.serverless.service;

import com.serverless.dto.ParkingDTO;
import java.util.List;

public interface ParkingService {

    List<ParkingDTO> getParkingList();
//    ParkingApiResponse getDataFromApi();
}
