package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Address;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByCountryAndCityAndStreetAndHouseAndFlat(String country, String city, String street,
                                                                   String house, String flat);
}
