package collector.model.sensors;

import collector.model.sensors.base.SensorEvent;
import collector.model.sensors.base.SensorType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClimateSensorEvent extends SensorEvent {
    @NotNull
    private Integer temperatureC;
    @NotNull
    private Integer humidity;
    @NotNull
    private Integer co2Level;

    @Override
    public SensorType getSensorType() {
        return SensorType.CLIMATE_SENSOR_EVENT;
    }
}
