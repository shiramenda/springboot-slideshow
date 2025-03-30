package com.sources.listeners;

import com.sources.events.AppActionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppActionEventListener {

    @EventListener
    public void handleAppActionEvent(AppActionEvent event) {
        System.out.printf(
                "[EVENT] %s | ID: %d | Data: %s%n",
                event.getActionType(),
                event.getResourceId(),
                event.getResourceNameOrUrl()
        );
    }
}
