package io.github.nguyennhatquang.fashion.Catalog.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.slugify.Slugify;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuAttribute;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SlugHepler {
    private static final Slugify slg = Slugify.builder().customReplacement("đ", "d").customReplacement("Đ", "d")
            .build();
    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String generateUniqueSlug(String title) {
        String baseSlug = slg.slugify(title);
        // Sinh chuỗi 7 ký tự (Tỉ lệ trùng lặp thực tế bằng 0)
        String uniqueSuffix = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, ALPHABET, 7);

        return baseSlug + "-" + uniqueSuffix;
    }

    public static String generateSkuCode(String brandName, String productId, List<SkuAttribute> attributes) {
        List<String> skuParts = new ArrayList<>();

        // 1. Xử lý PREFIX (Brand)
        String prefix = generateBrandPrefix(brandName);
        if (StringUtils.hasText(prefix)) {
            skuParts.add(prefix);
        }

        // 2. Xử lý PRODUCT CODE (Từ Product ID)
        String productCode = generateProductCode(productId);
        skuParts.add(productCode);

        // 3. Xử lý ATTRIBUTES (Size, Color, v.v...)
        if (attributes != null && !attributes.isEmpty()) {
            for (SkuAttribute attr : attributes) {
                // Giả định SkuAttribute của bạn có hàm getValue() trả về giá trị (vd: "Black",
                // "XL")
                String attrCode = generateAttributeCode(attr.getValue());
                if (StringUtils.hasText(attrCode)) {
                    skuParts.add(attrCode);
                }
            }
        }

        // 4. Nối lại bằng dấu gạch ngang và viết HOA toàn bộ
        return String.join("-", skuParts).toUpperCase();
    }

    // --- CÁC HÀM XỬ LÝ LOGIC BÊN TRONG ---

    private static String generateBrandPrefix(String brandName) {
        if (!StringUtils.hasText(brandName)) {
            return "NOB"; // NOB = No Brand
        }

        String cleanName = brandName.trim();
        String[] words = cleanName.split("\\s+"); // Tách các từ bằng khoảng trắng

        if (words.length > 1) {
            // Nếu tên có nhiều từ (VD: Louis Vuitton), lấy chữ cái đầu của tối đa 3 từ
            StringBuilder initials = new StringBuilder();
            int limit = Math.min(words.length, 3);
            for (int i = 0; i < limit; i++) {
                initials.append(words[i].charAt(0));
            }
            return initials.toString();
        } else {
            // Nếu tên có 1 từ (VD: Adidas), lấy tối đa 3 ký tự đầu tiên
            return cleanName.substring(0, Math.min(cleanName.length(), 3));
        }
    }

    private static String generateProductCode(String productId) {
        if (!StringUtils.hasText(productId)) {
            return "XXXXX";
        }
        // Lấy 5 ký tự cuối cùng của MongoDB ObjectId (rất khó trùng lặp trong cùng 1
        // Brand)
        if (productId.length() > 5) {
            return productId.substring(productId.length() - 5);
        }
        return productId;
    }

    private static String generateAttributeCode(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        String cleanValue = value.trim();

        // Nếu giá trị ngắn (như "S", "M", "XL", "Đỏ"), lấy luôn không cần cắt
        if (cleanValue.length() <= 3) {
            return cleanValue;
        }

        // Nếu giá trị dài (như "Black", "Cotton", "Oversize"), cắt lấy 3 ký tự đầu
        // Ghi chú: Có thể nâng cấp logic này để lọc bỏ nguyên âm (u,e,o,a,i) nếu muốn
        // SKU xịn hơn nữa
        return cleanValue.substring(0, 3);
    }
}
