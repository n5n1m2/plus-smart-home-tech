package collector.model.sensors;

import collector.model.sensors.base.SensorEvent;
import collector.model.sensors.base.SensorType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemperatureSensorEvent extends SensorEvent {
    @NotNull
    private Integer temperatureC;
    @NotNull
    private Integer temperatureF;

    @Override
    public SensorType getSensorType() {
        return SensorType.TEMPERATURE_SENSOR_EVENT;
    }
}
