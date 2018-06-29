package com.example.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class ExpiredReceiver implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredReceiver.class);

    @Override
    public void onMessage(Message message, byte[] bytes) {
        LOGGER.info("Expired Key={}", message);
    }
}
