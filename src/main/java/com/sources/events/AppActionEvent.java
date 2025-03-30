package com.sources.events;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppActionEvent {

    public enum ActionType {
        ADD_OR_UPDATE_IMAGE,
        DELETE_IMAGE,
        ADD_SLIDESHOW,
        DELETE_SLIDESHOW
    }

    private final ActionType actionType;
    private final Long resourceId;
    private final String resourceNameOrUrl;

    public AppActionEvent(ActionType actionType, Long resourceId, String resourceNameOrUrl) {
        this.actionType = actionType;
        this.resourceId = resourceId;
        this.resourceNameOrUrl = resourceNameOrUrl;
    }

}
