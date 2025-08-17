package collector.model.sensors;

import collector.model.sensors.base.SensorEvent;
import collector.model.sensors.base.SensorType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MotionSensorEvent extends SensorEvent {
    @NotNull
    private Integer linkQuality;
    @NotNull
    private Boolean motion;
    @NotNull
    private Integer voltage;

    @Override
    public SensorType getSensorType() {
        return SensorType.MOTION_SENSOR_EVENT;
    }
}
