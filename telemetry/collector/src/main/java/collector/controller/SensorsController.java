package collector.controller;

import collector.model.sensors.base.SensorEvent;
import collector.service.SensorEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events/sensors")
public class SensorsController {

    private final SensorEventService service;

    @PostMapping
    public ResponseEntity<String> sendSensorInfo(@RequestBody @Valid SensorEvent event) {
        service.sendToKafka(event);
        return ResponseEntity.ok().build();
    }
}
