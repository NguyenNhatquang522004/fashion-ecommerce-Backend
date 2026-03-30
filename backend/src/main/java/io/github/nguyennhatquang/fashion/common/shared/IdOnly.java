package io.github.nguyennhatquang.fashion.common.shared;

public interface IdOnly {
    String getId(); // Spring Data tự hiểu là mapping với _id của MongoDB
}
