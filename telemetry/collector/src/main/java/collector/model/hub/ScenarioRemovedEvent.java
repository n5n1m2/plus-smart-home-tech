package collector.model.hub;

import collector.model.hub.base.BaseHubEvent;
import collector.model.hub.base.EventType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScenarioRemovedEvent extends BaseHubEvent {

    @NotNull
    private String name;

    @Override
    public EventType getEventType() {
        return EventType.SCENARIO_REMOVED;
    }
}
