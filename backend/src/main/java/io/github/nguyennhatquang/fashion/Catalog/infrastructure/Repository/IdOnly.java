package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository;

public interface IdOnly {
    String getId(); // Spring Data tự hiểu là mapping với _id của MongoDB
}
