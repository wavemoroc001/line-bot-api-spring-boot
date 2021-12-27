package com.wavemoroc.linebot.repositories;

import com.wavemoroc.linebot.entities.ItemOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRepository extends JpaRepository<ItemOrder,Long> {
    List<ItemOrder> findByOwner(String ownerId);
}
