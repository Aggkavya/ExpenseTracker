package com.personal.Expense_Tracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /queue  = private user channels (one-to-one, used for notifications)
        // /topic  = broadcast channels (one-to-many, for future group features)
        config.enableSimpleBroker("/queue", "/topic");

        // Prefix for messages coming FROM frontend TO backend (@MessageMapping methods)
        config.setApplicationDestinationPrefixes("/app");

        // Prefix that Spring uses to route to a specific user
        // /user/{username}/queue/notifications
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // React connects to this URL to open the WebSocket
        // withSockJS() = fallback to HTTP polling if WebSocket is blocked
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
