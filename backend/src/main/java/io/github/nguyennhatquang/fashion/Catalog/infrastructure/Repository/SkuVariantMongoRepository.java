package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkuVariantMongoRepository extends MongoRepository<SkuVariant, String> {
        Optional<SkuVariant> findBySkuCode(String skuCode);

        List<SkuVariant> findByProductId(String productId);

        List<SkuVariant> findByProductIdAndIsActiveTrue(String productId);

        List<SkuVariant> findByProductIdAndIsDeletedFalse(String productId);

        List<SkuVariant> findByBarcode(String barcode);

        @Query("{ '_id' : ?0 }")
        @Update("{ '$set' : { 'isDeleted' : true } }")
        void softDeleteById(String id);

        @Query("{ 'sku_code' : ?0 }")
        @Update("{ '$set' : { 'isDeleted' : true } }")
        void softDeleteBySkuCode(String skuCode);

        @Query("{ 'productId' : ?0 }")
        @Update("{ '$set' : { 'isDeleted' : true } }")
        void softDeleteAllByProductId(String productId);

        @Query(value = "{ 'product_id' : ?0 }", count = true)
        long countByProductId(String productId);

        @Query(value = "{ 'created_at' : { $lte: ?0 } }", count = true)
        long countBySnapshot(Instant snapshotTime);

        // 2. Bước A: Lấy ID siêu tốc (Covered Query). fields = "{ '_id': 1 }" là chìa
        // khóa!
        @Query(value = "{ 'created_at' : { $lte: ?0 } }", fields = "{ '_id': 1 }")
        List<IdOnly> findIdsBySnapshot(Instant snapshotTime, Pageable pageable);

        // 3. Bước B: Lấy Full Data. (Lưu ý: Toán tử $in không đảm bảo thứ tự, nên phải
        // ép sort lại)
        @Query(value = "{ '_id' : { $in: ?0 } }", sort = "{ 'created_at': -1, '_id': -1 }")
        List<SkuVariant> fetchFullDataByIds(List<String> ids);

        // 1. Fetch NEXT page (DESC list)
        @Query(value = "{ " +
                        "'is_deleted': false, " +
                        "'$or': [ " +
                        "   { 'created_at': { $lt: ?0 } }, " +
                        "   { 'created_at': ?0, '_id': { $lt: ?1 } } " +
                        "] " +
                        "}", sort = "{ 'created_at': -1, '_id': -1 }")
        List<SkuVariant> findNextPageDesc(Instant cursorTime, String cursorId, Pageable pageable);

        // 2. Fetch PREVIOUS page (DESC list) -> Query ngược lại (ASC)

        @Query(value = "{ " +
                        "'is_deleted': false, " +
                        "'$or': [ " +
                        "   { 'created_at': { $gt: ?0 } }, " +
                        "   { 'created_at': ?0, '_id': { $gt: ?1 } } " +
                        "] " +
                        "}", sort = "{ 'created_at': 1, '_id': 1 }")
        List<SkuVariant> findPreviousPageDesc(Instant cursorTime, String cursorId, Pageable pageable);
}
