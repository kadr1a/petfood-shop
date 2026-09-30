package util;

import model.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public final class CsvExporter {

    private static final DateTimeFormatter DT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CsvExporter() {
    }

    public static Path exportOrders(List<Order> orders) throws IOException {
        Path dir = Path.of("exports");
        Files.createDirectories(dir);

        String fileName = "orders_" + System.currentTimeMillis() + ".csv";
        Path file = dir.resolve(fileName);

        List<String> lines = orders.stream()
                .map(CsvExporter::toLine)
                .collect(Collectors.toList());

        String header = "id;buyer_id;product_name;quantity;total_price;status;created_at";
        lines.add(0, header);

        Files.write(file, lines);
        return file.toAbsolutePath();
    }

    private static String toLine(Order order) {
        String created = order.getCreatedAt() != null
                ? order.getCreatedAt().format(DT_FORMAT)
                : "";
        return String.join(";",
                String.valueOf(order.getId()),
                String.valueOf(order.getBuyerId()),
                escape(order.getProductName()),
                String.valueOf(order.getQuantity()),
                order.getTotalPrice().toPlainString(),
                order.getStatus().name(),
                created
        );
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(";") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
