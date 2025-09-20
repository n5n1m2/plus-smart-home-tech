package warehouse.model;

import interaction.model.warehouse.DimensionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Dimension dtoToDimension(DimensionDto dto);

    DimensionDto dimensionToDto(Dimension dimension);
}
