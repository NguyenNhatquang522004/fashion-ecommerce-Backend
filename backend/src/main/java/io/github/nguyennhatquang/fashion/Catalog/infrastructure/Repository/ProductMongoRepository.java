package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.common.Enum.ProductStatusEnum;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMongoRepository extends MongoRepository<Product, String> {
        Optional<Product> findBySlug(String slug);

        Optional<Product> findByStatus(ProductStatusEnum status);

        List<Product> findByCategoryIdsContainingAndIsDeletedFalse(String categoryId);

        @Query("{ 'brand_id': ?0, 'is_deleted': false }")
        List<Product> findProductsWithExactlyOneSpecificBrand(String brandId);

        @Query(value = "{ 'category_ids': ?0, 'is_deleted': false }", fields = "{ '_id': 1 }")
        List<Product> findIdsByCategoryId(String categoryId);

        @Query("{ 'category_ids': ?0, 'is_deleted': false }")
        @Update("{ '$set': { 'is_deleted': true, 'updated_at': new java.util.Date() } }")
        void softDeleteAllByCategoryId(String categoryId);

        @Query("{ 'category_ids': [ ?0 ], 'is_deleted': false }")
        List<Product> findProductsWithExactlyOneSpecificCategory(String categoryId);

        @Query(value = "{ 'created_at' : { $lte: ?0 } }", count = true)
        long countBySnapshot(Instant snapshotTime);

        @Query("{ '_id': { $in: ?0 } }")
        @Update("{ '$set': { 'isDeleted': true } }")
        void softDeleteAllByIds(List<String> productIds);

        @Query("{ '_id' : ?0 }")
        @Update("{ '$set' : { 'isDeleted' : true } }")
        void softDeleteById(String id);

        @Query("{ 'slug' : ?0 }")
        @Update("{ '$set' : { 'isDeleted' : true } }")
        void softDeleteBySlug(String slug);

        // 2. Bước A: Lấy ID siêu tốc (Covered Query). fields = "{ '_id': 1 }" là chìa
        // khóa!
        @Query(value = "{ 'created_at' : { $lte: ?0 } }", fields = "{ '_id': 1 }")
        List<IdOnly> findIdsBySnapshot(Instant snapshotTime, Pageable pageable);

        // 3. Bước B: Lấy Full Data. (Lưu ý: Toán tử $in không đảm bảo thứ tự, nên phải
        // ép sort lại)
        @Query(value = "{ '_id' : { $in: ?0 } }", sort = "{ 'created_at': -1, '_id': -1 }")
        List<Product> fetchFullDataByIds(List<String> ids);

        // 1. Fetch NEXT page (DESC list)
        @Query(value = "{ " +
                        "'is_deleted': false, " +
                        "'$or': [ " +
                        "   { 'created_at': { $lt: ?0 } }, " +
                        "   { 'created_at': ?0, '_id': { $lt: ?1 } } " +
                        "] " +
                        "}", sort = "{ 'created_at': -1, '_id': -1 }")
        List<Product> findNextPageDesc(Instant cursorTime, String cursorId, Pageable pageable);

        // 2. Fetch PREVIOUS page (DESC list) -> Query ngược lại (ASC)

        @Query(value = "{ " +
                        "'is_deleted': false, " +
                        "'$or': [ " +
                        "   { 'created_at': { $gt: ?0 } }, " +
                        "   { 'created_at': ?0, '_id': { $gt: ?1 } } " +
                        "] " +
                        "}", sort = "{ 'created_at': 1, '_id': 1 }")
        List<Product> findPreviousPageDesc(Instant cursorTime, String cursorId, Pageable pageable);
}
