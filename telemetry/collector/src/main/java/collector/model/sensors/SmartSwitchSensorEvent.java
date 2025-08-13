package collector.model.sensors;

import collector.model.sensors.base.SensorEvent;
import collector.model.sensors.base.SensorType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmartSwitchSensorEvent extends SensorEvent {
    @NotNull
    private Boolean state;

    @Override
    public SensorType getSensorType() {
        return SensorType.SWITCH_SENSOR_EVENT;
    }
}
