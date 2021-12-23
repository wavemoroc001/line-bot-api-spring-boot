package com.wavemoroc.linebot.repositories;

import com.wavemoroc.linebot.entities.ItemOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<ItemOrder,Long> {
    List<ItemOrder> findByOwner(String ownerId);
}
