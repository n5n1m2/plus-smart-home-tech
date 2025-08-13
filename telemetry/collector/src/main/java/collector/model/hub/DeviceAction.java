package collector.model.hub;

import collector.model.hub.base.ActionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAction {
    private String sensorId;

    private ActionType type;

    private Integer value;
}
