package fr.robotv2.common.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableInfo;
import com.j256.ormlite.table.TableUtils;
import fr.robotv2.common.data.impl.MySqlCredentials;
import fr.robotv2.common.data.impl.SqlLiteCredentials;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class OrmData<D, ID> {

    private Dao<D, ID> dao;
    private ConnectionSource source;

    public void initialize(@NotNull ConnectionSource source, @NotNull Class<D> clazz, boolean checkColumns) throws SQLException {
        this.source = source;
        this.dao = DaoManager.createDao(source, clazz);
        TableUtils.createTableIfNotExists(source, clazz);

        if(checkColumns) {
            checkAndUpdateTable();
        }
    }

    public void initialize(@NotNull ConnectionSource source, @NotNull Class<D> clazz) throws SQLException {
        initialize(source, clazz, false);
    }

    public CompletableFuture<D> get(ID identification) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.dao.queryForId(identification);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<List<D>> getWhere(Function<Where<D, ID>, Where<D, ID>> function) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final QueryBuilder<D, ID> builder = this.getDao().queryBuilder();
                builder.setWhere(function.apply(builder.where()));
                return builder.query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        });
    }

    public void remove(D data) {
        CompletableFuture.runAsync(() -> {
            try {
                this.dao.delete(data);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> removeWhere(Function<Where<D, ID>, Where<D, ID>> function) {
        return CompletableFuture.runAsync(() -> {
            try {
                final DeleteBuilder<D, ID> builder = this.getDao().deleteBuilder();
                builder.setWhere(function.apply(builder.where()));
                builder.delete();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void save(D data) {
        try {
            this.dao.createOrUpdate(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> saveAsync(D data) {
        return CompletableFuture.runAsync(() -> {
            save(data);
        });
    }

    public Dao<D, ID> getDao() {
        return this.dao;
    }

    private void checkAndUpdateTable() throws SQLException {

        final String tableName = getDao().getTableName();
        final TableInfo<D, ID> tableInfo = getDao().getTableInfo();

        for (FieldType field : tableInfo.getFieldTypes()) {

            final String columnName = field.getFieldName();

            // Check if the column exists
            if (!this.columnExists(tableName, columnName)) {

                String columnDefinition = field.getColumnDefinition();
                if (columnDefinition == null) {
                    columnDefinition = field.getDataPersister().getSqlType() + " " + (field.isCanBeNull() ? "NULL" : "NOT NULL");
                }

                final String addColumnSQL = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition;
                getDao().executeRaw(addColumnSQL);
            }
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        final String name = source.getDatabaseType().getDatabaseName();

        if(name.equalsIgnoreCase("MySQL")) {
            return MySqlCredentials.columnExists(source, tableName, columnName);
        }

        if(name.equalsIgnoreCase("SQLite")) {
            return SqlLiteCredentials.columnExists(source, tableName, columnName);
        }

        throw new SQLException("An error occurred while updating columns. '" + name + "' is not a valid database type.");
    }
}