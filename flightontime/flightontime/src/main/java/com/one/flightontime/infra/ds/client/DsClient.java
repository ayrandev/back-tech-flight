package com.one.flightontime.infra.ds.client;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "dsClient", url = "https://data-tech-flight-production.up.railway.app")
public interface DsClient {

    @PostMapping("/predict")
    PredictionResponse predict(@RequestBody PredictionRequest request);

}
