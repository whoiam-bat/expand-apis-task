package com.example.task.service;

import com.example.task.dao.RecordDao;
import com.example.task.exception.DbException;
import com.example.task.exception.EntityNotFound;
import com.example.task.model.dto.Request;
import com.example.task.model.dto.RequestRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordDao recordDao;

    private void createTable(String tableName, List<RequestRecord> records) throws SQLException {
        List<String> columns = Arrays.stream(records.get(0).getClass().getDeclaredFields())
                .map(Field::getName)
                .toList();

        recordDao.createTableDynamically(tableName, columns);
    }

    public void addRecords(Request request) throws DbException {
        try {
            String tableName = request.getTable();
            List<RequestRecord> records = request.getRecords();

            createTable(tableName, records);

            List<Map<String, String>> recordFieldValuesMap = records.stream()
                    .map(record -> {
                        List<Field> recordFields = List.of(record.getClass().getDeclaredFields());

                        Map<String, String> fieldValuesMap = new HashMap<>();
                        recordFields.forEach(field -> {
                            field.setAccessible(true);
                            try {
                                Object value = field.get(record);
                                String fieldName = field.getName();
                                String fieldValue = (value != null) ? value.toString() : null;
                                fieldValuesMap.put(fieldName, fieldValue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } finally {
                                field.setAccessible(false);
                            }
                        });

                        return fieldValuesMap;
                    })
                    .toList();

            for (Map<String, String> record : recordFieldValuesMap)
                recordDao.addRecord(tableName, record);

        } catch (SQLException ex) {
            throw new DbException(ex.getLocalizedMessage());
        }
    }

    public List<RequestRecord> findAll() throws EntityNotFound {
        try {
            return recordDao.findAll();
        } catch (SQLException ex) {
            throw new EntityNotFound(ex.getLocalizedMessage());
        }
    }

}
