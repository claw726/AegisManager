package com.aegis.project.configuration;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private static final Logger logger = LoggerFactory.getLogger(
    WebSocketConfig.class
  );

  @Autowired
  private WebSocketInterceptor webSocketInterceptor;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
      .addEndpoint("/ws")
      .setAllowedOriginPatterns("*")
      .withSockJS()
      .setStreamBytesLimit(512 * 1024)
      .setHttpMessageCacheSize(1000)
      .setDisconnectDelay(30 * 1000)
      .setInterceptors(
        new HandshakeInterceptor() {
          @Override
          public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
          ) throws Exception {
            logger.info("Handshake request received: {}", request.getURI());
            return true;
          }

          @Override
          public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
          ) {
            if (exception != null) {
              logger.error("Handshake failed: {}", exception.getMessage());
            } else {
              logger.info("Handshake successful");
            }
          }
        }
      );
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(webSocketInterceptor);
  }

  @Override
  public void configureWebSocketTransport(
    WebSocketTransportRegistration registry
  ) {
    registry.setMessageSizeLimit(512 * 1024);
    registry.setSendBufferSizeLimit(1024 * 1024);
    registry.setSendTimeLimit(20 * 1000);
  }
}
