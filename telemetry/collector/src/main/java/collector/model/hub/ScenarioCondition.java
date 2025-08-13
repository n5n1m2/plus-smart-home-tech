package collector.model.hub;

import collector.model.hub.base.ConditionOperation;
import collector.model.hub.base.ConditionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScenarioCondition {
    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private Object value;
}
