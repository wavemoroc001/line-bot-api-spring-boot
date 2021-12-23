package com.wavemoroc.linebot.repositories;

import com.wavemoroc.linebot.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item,Long> {
}
