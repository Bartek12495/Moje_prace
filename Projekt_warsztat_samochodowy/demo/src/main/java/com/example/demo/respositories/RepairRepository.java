package com.example.demo.respositories;

import com.example.demo.models.entities.Repair;
import com.example.demo.models.entities.Repair.StatusNaprawy;
import com.example.demo.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Integer> {

    // Znajdź wszystkie naprawy przypisane do danego mechanika
    List<Repair> findByMechanic(User mechanic);

    // Znajdź wszystkie naprawy przypisane do danego mechanika o konkretnym statusie
    List<Repair> findByMechanicAndStatus(User mechanic, StatusNaprawy status);

    //Znajdź wszystkie naprawy powiązane z samochodami należącymi do danego klienta
    List<Repair> findBySamochodOwnerId(Integer userId);
}
