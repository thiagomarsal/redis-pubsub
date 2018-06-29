package com.example.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class ChatReceiver implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatReceiver.class);

    @Override
    public void onMessage(Message message, byte[] bytes) {
        LOGGER.info("Received Message={}", message);
    }
}
