package com.megacart;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class ConnectionTimingTest implements CommandLineRunner {

    private final DataSource dataSource;

    public ConnectionTimingTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 1; i <= 3; i++) {
            long startTotal = System.currentTimeMillis();

            long startConn = System.currentTimeMillis();
            try (Connection conn = dataSource.getConnection()) {
                long endConn = System.currentTimeMillis();

                long startQuery = System.currentTimeMillis();
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    while (rs.next()) {
                        rs.getInt(1); // đọc kết quả
                    }
                }
                long endQuery = System.currentTimeMillis();

                long endTotal = System.currentTimeMillis();

                System.out.printf("Lần %d:%n", i);
                System.out.printf("  Thời gian lấy connection là: %d ms%n", (endConn - startConn));
                System.out.printf("  Thời gian thực thi query là: %d ms%n", (endQuery - startQuery));
                System.out.printf("  Tổng thời gian là: %d ms%n%n", (endTotal - startTotal));
            }
        }
    }
}
