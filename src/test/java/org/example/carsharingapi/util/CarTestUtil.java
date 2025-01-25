package org.example.carsharingapi.util;

import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.model.enums.CarType;

import java.math.BigDecimal;
import java.util.List;

public class CarTestUtil {
    public static List<CarDto> getCarDtoList() {
        CarDto carDto1 = new CarDto();
        carDto1.setId(1L);
        carDto1.setBrand("Tesla");
        carDto1.setModel("Model S");
        carDto1.setType(CarType.HATCHBACK);
        carDto1.setInventory(10);
        carDto1.setDailyFee(BigDecimal.valueOf(100));

        CarDto carDto2 = new CarDto();
        carDto2.setId(2L);
        carDto2.setBrand("Tesla 1");
        carDto2.setModel("Model S 1");
        carDto2.setType(CarType.SUV);
        carDto2.setInventory(0);
        carDto2.setDailyFee(BigDecimal.valueOf(120));

        CarDto carDto3 = new CarDto();
        carDto3.setId(3L);
        carDto3.setBrand("Tesla 2");
        carDto3.setModel("Model S 2");
        carDto3.setType(CarType.SEDAN);
        carDto3.setInventory(10);
        carDto3.setDailyFee(BigDecimal.valueOf(30));

        return List.of(carDto1, carDto2, carDto3);
    }
}
