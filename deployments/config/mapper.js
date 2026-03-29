module.exports = function(doc, meta) {
    // Chỉ đồng bộ dữ liệu vào index product_catalog_index
    meta.index = "product_catalog_index";
    
    var db = client.Database("fashion");
    var productId;

    // 1. Xác định sự kiện đến từ Collection nào
    if (meta.namespace === "fashion.products") {
        productId = doc._id;
    } else if (meta.namespace === "fashion.sku_variants") {
        productId = doc.product_id; 
        meta.id = productId; // Quan trọng: Đổi ID đích thành ID của Product
    } else {
        return false; // Bỏ qua nếu không đúng namespace
    }

    // 2. Truy vấn lấy Product gốc
    var product = db.Collection("products").FindOne({"_id": productId});
    if (!product) return false;

    // 3. Mapping Brand
    var brand = null;
    if (product.brand_id) {
        var b = db.Collection("brands").FindOne({"_id": product.brand_id});
        if (b) brand = { id: b._id.Hex(), name: b.name, slug: b.slug };
    }

    // 4. Mapping Categories
    var categories = [];
    if (product.category_ids && product.category_ids.length > 0) {
        for (var i = 0; i < product.category_ids.length; i++) {
            var c = db.Collection("categories").FindOne({"_id": product.category_ids[i]});
            if (c) categories.push({ id: c._id.Hex(), name: c.name, slug: c.slug, path: c.path });
        }
    }

    // 5. Mapping Variants & Tính toán Khoảng giá (min_price, max_price)
    var variants = [];
    var minPrice = null;
    var maxPrice = null;
    
    // Chỉ lấy các Variant đang active và chưa bị soft delete
    var variantCursor = db.Collection("sku_variants").Find({
        "product_id": productId, 
        "is_deleted": false, 
        "is_active": true
    });

    while (variantCursor.Next()) {
        var v = variantCursor.Document();
        variants.push({
            sku_code: v.sku_code,
            barcode: v.barcode || null,
            price_override: v.price_override ? parseFloat(v.price_override) : null,
            attributes: v.attributes || []
        });

        // Xử lý logic giá
        var price = v.price_override ? parseFloat(v.price_override) : parseFloat(product.base_price);
        if (minPrice === null || price < minPrice) minPrice = price;
        if (maxPrice === null || price > maxPrice) maxPrice = price;
    }
    
    // Nếu không có variant nào có giá riêng, dùng base_price
    if (minPrice === null) minPrice = parseFloat(product.base_price);
    if (maxPrice === null) maxPrice = parseFloat(product.base_price);

    // Xử lý Thumbnail
    var thumbnailUrl = null;
    if (product.media && product.media.length > 0) {
        for (var j = 0; j < product.media.length; j++) {
            if (product.media[j].is_thumbnail) {
                thumbnailUrl = product.media[j].url;
                break;
            }
        }
        if (!thumbnailUrl) thumbnailUrl = product.media[0].url; // Fallback ảnh đầu tiên
    }

    // 6. Trả về Object chuẩn 100% khớp với Elasticsearch Mapping
    return {
        id: product._id.Hex(),
        name: product.name,
        slug: product.slug,
        description: product.description || null,
        thumbnail_url: thumbnailUrl,
        brand: brand,
        categories: categories,
        pricing: {
            base_price: parseFloat(product.base_price),
            min_price: minPrice,
            max_price: maxPrice
        },
        attributes: product.attributes || [],
        variants: variants,
        status: product.status,
        is_active: product.is_active,
        is_deleted: product.is_deleted,
        created_at: product.created_at,
        updated_at: product.updated_at
    };
}