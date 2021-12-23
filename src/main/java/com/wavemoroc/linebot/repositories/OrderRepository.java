package com.wavemoroc.linebot.repositories;

import com.wavemoroc.linebot.entities.ItemOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<ItemOrder,Long> {
}
