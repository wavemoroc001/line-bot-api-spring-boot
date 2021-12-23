package com.wavemoroc.linebot.services;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.wavemoroc.linebot.entities.Item;
import com.wavemoroc.linebot.entities.ItemOrder;
import com.wavemoroc.linebot.repositories.OrderRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@LineMessageHandler
@Slf4j
public class LineBotController {

    @Autowired
    private LineMessagingClient lineMessagingClient;
    @Autowired
    private OrderRepository orderRepository;

    @EventMapping
    public void handleTextMessage(MessageEvent<TextMessageContent> event) {
        log.info("event: " + event);
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);
    }

    private void handleTextContent(String replyToken, Event event,
                                   TextMessageContent content) {

        String text = content.getText();

        log.info("Got text message from %s : %s", replyToken, text);

        switch (text) {
            case "whoami": {
                String userId = event.getSource().getUserId();
                if (userId != null) {
                    lineMessagingClient.getProfile(userId)
                            .whenComplete((profile, throwable) -> {
                                if (throwable != null) {
                                    replyText(replyToken, throwable.getMessage());
                                    return;
                                }
                                reply(replyToken, Arrays.asList(
                                        new TextMessage("Display name: " +
                                                profile.getDisplayName()),
                                        new TextMessage("Status message: " +
                                                profile.getStatusMessage()),
                                        new TextMessage("User ID: " +
                                                profile.getUserId())
                                ));
                            });
                }
                break;
            }
            case "order": {
                String userId = event.getSource().getUserId();
                if (userId != null) {
                    lineMessagingClient.getProfile(userId)
                            .whenComplete((profile, throwable) -> {
                                if (throwable != null) {
                                    replyText(replyToken, throwable.getMessage());
                                    return;
                                }
                                List<ItemOrder> itemOrderList = orderRepository.findByOwner(userId);
                                List<Message> textMessageList = new ArrayList<>();
                                StringBuilder builder = new StringBuilder();
                                for(ItemOrder itemOrder : itemOrderList) {
                                   builder.append("OrderID :" + itemOrder.getId()+ "\n");
//                                    builder.append("------------------------------\n");
//
//                                    for(Item item : itemOrder.getItemList()) {
//                                        builder.append(item.getName()+ "\t" +item.getPrice() +"\n" );
//                                    }
                                    reply(replyToken, new TextMessage(builder.toString()));

                                }
                            });
                }
                break;
            }
            default:
                log.info("Return echo message %s : %s", replyToken, text);
                replyText(replyToken, text);
        }

    }


    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken is not empty");
        }

        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "...";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
            BotApiResponse response = lineMessagingClient.replyMessage(
                    new ReplyMessage(replyToken, messages)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @EventMapping
    public void handleStickerMessage(MessageEvent<StickerMessageContent> event) {
        log.info(event.toString());
        StickerMessageContent message = event.getMessage();
        reply(event.getReplyToken(), new StickerMessage(
                message.getPackageId(), message.getStickerId()
        ));
    }

}
