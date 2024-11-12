package com.aegis.project.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketEventListener {

  private static final Logger logger = LoggerFactory.getLogger(
    WebSocketEventListener.class
  );

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    logger.info("Received a new web socket connection request");
  }

  @EventListener
  public void handleWebSocketConnectedListener(SessionConnectedEvent event) {
    logger.info("New web socket connection established");
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    logger.info("User disconnected from web socket");
  }

  @EventListener
  public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
    logger.info(
      "User subscribed to channel: {}",
      event.getMessage().getHeaders().get("simpDestination")
    );
  }
}
