package fr.robotv2.common.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class OrmData<D, ID> {

    private Dao<D, ID> dao;

    public void initialize(@NotNull ConnectionSource source, @NotNull Class<D> clazz) throws SQLException {
        this.dao = DaoManager.createDao(source, clazz);
        TableUtils.createTableIfNotExists(source, clazz);
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
}