package collector.controller.http;

import collector.model.hub.base.BaseHubEvent;
import collector.service.HubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events/hubs")
public class HubController {
    private final HubService hubService;

    @PostMapping
    public ResponseEntity<String> sendHubEvent(@RequestBody @Valid BaseHubEvent hubEvent) {
        hubService.sendToKafka(hubEvent);
        return ResponseEntity.ok().build();
    }
}
