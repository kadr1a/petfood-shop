package util;

import model.Order;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ExcelExporter {

    private static final DateTimeFormatter DT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ExcelExporter() {
    }

    public static Path exportOrders(List<Order> orders) throws IOException {
        Path dir = Path.of("exports");
        Files.createDirectories(dir);

        String fileName = "orders_" + System.currentTimeMillis() + ".xlsx";
        Path file = dir.resolve(fileName);

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream out = Files.newOutputStream(file)) {

            Sheet sheet = workbook.createSheet("Заказы");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("ID покупателя");
            header.createCell(2).setCellValue("Товар");
            header.createCell(3).setCellValue("Количество");
            header.createCell(4).setCellValue("Сумма");
            header.createCell(5).setCellValue("Статус");
            header.createCell(6).setCellValue("Дата создания");

            int rowIdx = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getBuyerId());
                row.createCell(2).setCellValue(order.getProductName());
                row.createCell(3).setCellValue(order.getQuantity());
                row.createCell(4).setCellValue(order.getTotalPrice().doubleValue());
                row.createCell(5).setCellValue(order.getStatus().name());
                String created = order.getCreatedAt() != null
                        ? order.getCreatedAt().format(DT_FORMAT)
                        : "";
                row.createCell(6).setCellValue(created);
            }

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        }

        return file.toAbsolutePath();
    }
}
