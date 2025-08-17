package collector.model.hub;

import collector.model.hub.base.BaseHubEvent;
import collector.model.hub.base.EventType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRemoveEvent extends BaseHubEvent {

    @NotNull
    private String id;

    @Override
    public EventType getEventType() {
        return EventType.DEVICE_REMOVED;
    }
}
