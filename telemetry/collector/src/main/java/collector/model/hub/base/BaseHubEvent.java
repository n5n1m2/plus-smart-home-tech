package collector.model.hub.base;

import collector.model.hub.DeviceAddedEvent;
import collector.model.hub.DeviceRemoveEvent;
import collector.model.hub.ScenarioAddedEvent;
import collector.model.hub.ScenarioRemovedEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemoveEvent.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})
@Getter
@Setter
public abstract class BaseHubEvent {

    private final Instant timestamp = Instant.now();

    @NotNull
    private String hubId;

    public abstract EventType getEventType();
}
