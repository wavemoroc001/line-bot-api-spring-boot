package com.wavemoroc.linebot.services;

import com.wavemoroc.linebot.entities.Item;
import com.wavemoroc.linebot.entities.ItemOrder;
import com.wavemoroc.linebot.repositories.ItemRepository;
import com.wavemoroc.linebot.repositories.OrderRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class InitialService {

    @Bean
    CommandLineRunner runner (OrderRepository orderRepository,
                              ItemRepository itemRepository,
                              RestTemplateBuilder restTemplateBuilder) {
        return args -> {
            List<Item> itemList = new ArrayList<>();
            List<Item> itemList1 = new ArrayList<>();
            List<ItemOrder> itemOrderList = new ArrayList<>();

            itemList.add(new Item("Chocolate",20d));
            itemList.add(new Item("Guitar",2_000d));
            itemList = itemRepository.saveAll(itemList);

            itemList1.add(new Item("Fired Chicken",90d));
            itemList1.add(new Item("Pen",20d));
            itemList1.add(new Item("Pineapple",30d));
            itemList1 = itemRepository.saveAll(itemList1);

            itemOrderList.add(new ItemOrder(itemList,"Ua529dd4a42816ca89f3efe1127b3df92"));
            itemOrderList.add(new ItemOrder(itemList1,"Ua529dd4a42816ca89f3efe1127b3df92"));

            orderRepository.saveAll(itemOrderList);
            testDB(orderRepository);


        };
    }

    @Transactional
    void testDB(@NonNull OrderRepository orderRepository) {
        // test
        List<ItemOrder> getOrderList  = orderRepository.findByOwner("Ua529dd4a42816ca89f3efe1127b3df92");
        for (ItemOrder itemOrder : getOrderList) {
            StringBuilder builder = new StringBuilder();
            builder.append("\nOrderID :" + itemOrder.getId());
            builder.append("\n------------------------------\n");

            for (Item item : itemOrder.getItemList()) {
                builder.append(item.getName() + "\t" + item.getPrice() + "\n");
//                log.error(item.getName());
//                log.error(item.getPrice().toString());
            }
            log.error(builder.toString());
        }
    }
}
