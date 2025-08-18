package com.megacart.utils;

import com.megacart.model.AnhMinhHoa;

import java.util.Comparator;
import java.util.List;

public class ImageUtils {

    /**
     * Lấy URL của ảnh chính từ danh sách ảnh minh họa.
     * Nếu không có ảnh nào được đánh dấu là chính, nó sẽ lấy ảnh đầu tiên theo số thứ tự.
     * Nếu không có ảnh nào, trả về null.
     * @param anhMinhHoas Danh sách các đối tượng AnhMinhHoa.
     * @return URL của ảnh chính, hoặc ảnh đầu tiên, hoặc null.
     */
    public static String getAnhMinhHoaChinhUrl(List<AnhMinhHoa> anhMinhHoas) {
        if (anhMinhHoas == null || anhMinhHoas.isEmpty()) {
            return null; // Hoặc một URL placeholder mặc định
        }

        return anhMinhHoas.stream().filter(AnhMinhHoa::isLaAnhChinh).findFirst().map(AnhMinhHoa::getDuongDan)
                .orElseGet(() -> anhMinhHoas.stream().min(Comparator.comparing(AnhMinhHoa::getSoThuTu, Comparator.nullsLast(Comparator.naturalOrder()))).map(AnhMinhHoa::getDuongDan).orElse(null));
    }
}