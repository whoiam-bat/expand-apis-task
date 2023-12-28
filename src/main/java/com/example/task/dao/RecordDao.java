package com.example.task.dao;

import com.example.task.model.dto.RequestRecord;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Repository
@RequiredArgsConstructor
public class RecordDao {

    private final DataSource dataSource;

    @Transactional
    public void createTableDynamically(String tableName, List<String> columns) throws SQLException {
        String query = String.format("CREATE TABLE IF NOT EXISTS `%s` (`id` int PRIMARY KEY AUTO_INCREMENT NOT NULL, %s)",
                tableName,
                String.join(", ", columns.stream()
                        .map(column -> "`" + column + "` varchar(255) NOT NULL")
                        .toList()
                )
        );

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(query);
        }
    }

    @Transactional
    public void addRecord(String tableName, Map<String, String> records) throws SQLException {
        int size = records.size();

        String query = String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                String.join(",", records.keySet()),
                String.join(",", Collections.nCopies(records.size(), "?")));

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int parameterIndex = 1;

            for (String value : records.values())
                preparedStatement.setString(parameterIndex++, value);

            preparedStatement.executeUpdate();
        }
    }

    public List<RequestRecord> findAll() throws SQLException {
        String query = "SELECT * FROM products";

        List<RequestRecord> records = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {


            while (resultSet.next()) {
                records.add(
                        RequestRecord.builder()
                                .entryDate("entryDate")
                                .itemCode("itemCode")
                                .itemName("itemName")
                                .itemQuantity("itemQuantity")
                                .status("status")
                                .build()
                );
            }
        }

        return records;
    }
}
