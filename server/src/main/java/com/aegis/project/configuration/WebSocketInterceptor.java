package com.aegis.project.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class WebSocketInterceptor implements ChannelInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(
    WebSocketInterceptor.class
  );

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
      message,
      StompHeaderAccessor.class
    );

    if (accessor != null && accessor.getCommand() != null) {
      switch (accessor.getCommand()) {
        case CONNECT:
          logger.info("WebSocket CONNECT request received");
          logger.debug(
            "Connect details - Session ID: {}, User: {}",
            accessor.getSessionId(),
            accessor.getUser()
          );
          break;
        case SUBSCRIBE:
          logger.info(
            "WebSocket SUBSCRIBE request received for destination: {}",
            accessor.getDestination()
          );
          break;
        case DISCONNECT:
          logger.info(
            "WebSocket DISCONNECT request received - Session ID: {}",
            accessor.getSessionId()
          );
          break;
      }
    }

    return message;
  }
}
