package collector.model.sensors;

import collector.model.sensors.base.SensorEvent;
import collector.model.sensors.base.SensorType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LightSensorEvent extends SensorEvent {
    private Integer linkQuality;
    private Integer luminosity;

    @Override
    public SensorType getSensorType() {
        return SensorType.LIGHT_SENSOR_EVENT;
    }
}
