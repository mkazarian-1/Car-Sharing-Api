package org.example.carsharingapi.mapper;

import org.example.carsharingapi.config.MapperConfig;
import org.example.carsharingapi.dto.rental.CreateRequestRentalDto;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.dto.rental.UpdateRequestRentalDto;
import org.example.carsharingapi.model.Car;
import org.example.carsharingapi.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = CarMapper.class)
public interface RentalMapper {
    @Mapping(source = "user.id", target = "userId")
    RentalDto toDto(Rental rental);

    @Mapping(source = "carId", target = "car", qualifiedByName = "setCarWithId")
    Rental toEntity(CreateRequestRentalDto requestRentalDto);

    @Named("setCarWithId")
    default Car setCarWithId(Long carId) {
        Car newCar = new Car();
        newCar.setId(carId);
        return newCar;
    }

    void updateRental(@MappingTarget Rental rental, UpdateRequestRentalDto requestRentalDto);
}
