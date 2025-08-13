package collector.model.hub;

import collector.model.hub.base.BaseHubEvent;
import collector.model.hub.base.EventType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScenarioAddedEvent extends BaseHubEvent {

    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private List<ScenarioCondition> conditions;

    @NotEmpty
    @NotNull
    private List<DeviceAction> actions;

    @Override
    public EventType getEventType() {
        return EventType.SCENARIO_ADDED;
    }
}
