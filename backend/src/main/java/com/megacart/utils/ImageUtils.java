package com.megacart.utils;

import com.megacart.model.AnhMinhHoa;

import java.util.List;

public final class ImageUtils {

    private ImageUtils() {
        // Private constructor để ngăn việc tạo instance của utility class
    }

    // Mã hóa URL cho ảnh sản phẩm

    /**
     * Lấy URL của ảnh đại diện từ một danh sách ảnh.
     * Ưu tiên ảnh được đánh dấu là 'laAnhChinh'.
     * Nếu không có, lấy ảnh đầu tiên trong danh sách làm ảnh đại diện.
     * @param anhMinhHoas Danh sách các ảnh minh họa của sản phẩm.
     * @return URL của ảnh đại diện, hoặc null nếu không có ảnh nào.
     */
    public static String getAnhMinhHoaChinhUrl(List<AnhMinhHoa> anhMinhHoas) {
        if (anhMinhHoas == null || anhMinhHoas.isEmpty()) {
            return null;
        }
        return anhMinhHoas.stream().filter(AnhMinhHoa::isLaAnhChinh).findFirst().map(AnhMinhHoa::getDuongDan)
                .orElse(anhMinhHoas.get(0).getDuongDan());
    }
}