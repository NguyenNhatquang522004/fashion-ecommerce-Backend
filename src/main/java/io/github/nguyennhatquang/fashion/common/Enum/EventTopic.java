package io.github.nguyennhatquang.fashion.common.Enum;

public enum EventTopic {
    User("user"), Product("product"), Order("order");

    private final String topicName;

    EventTopic(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
