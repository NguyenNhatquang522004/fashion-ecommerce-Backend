package io.github.nguyennhatquang.fashion.common.Enum;

public enum EventType {
    Created("Event.created"), Updated("Event.updated"), Deleted("Event.deleted");

    private final String topicName;

    EventType(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
