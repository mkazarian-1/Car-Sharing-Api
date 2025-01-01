package org.example.carsharingapi.service;

import java.time.LocalDate;
import java.util.List;
import org.example.carsharingapi.model.Rental;

public interface OverdueRentalAnalysisService {
    List<Rental> findOverdueRentals(LocalDate expiryDate);
}
