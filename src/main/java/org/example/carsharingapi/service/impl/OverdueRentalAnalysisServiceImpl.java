package org.example.carsharingapi.service.impl;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.repository.RentalRepository;
import org.example.carsharingapi.service.OverdueRentalAnalysisService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OverdueRentalAnalysisServiceImpl implements OverdueRentalAnalysisService {
    private final RentalRepository rentalRepository;

    @Override
    public List<Rental> findOverdueRentals(LocalDate expiryDate) {
        return rentalRepository.findOverdueRentals(expiryDate);
    }
}
