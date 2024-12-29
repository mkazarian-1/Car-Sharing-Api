package org.example.carsharingapi.mapper;

import org.example.carsharingapi.config.MapperConfig;
import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.dto.car.CreateRequestCarDto;
import org.example.carsharingapi.dto.car.UpdateRequestCarDto;
import org.example.carsharingapi.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toEntity(CreateRequestCarDto requestCarDto);

    void updateCar(UpdateRequestCarDto requestCarDto, @MappingTarget Car car);

    List<CarDto> toListDto(List<Car> cars);
}
