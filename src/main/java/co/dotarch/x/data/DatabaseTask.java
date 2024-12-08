package co.dotarch.x.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseTask {

    private final static Task emptyTask = ps -> {
    };
    private final String statement;
    private final Task task;

    public DatabaseTask(String statement, Task task) {
        this.statement = statement;
        this.task = task;
    }

    public DatabaseTask(String statement) {
        this.statement = statement;
        this.task = emptyTask;
    }


    public void run(Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(statement)) { //TODO Use addBatch to improve performance
            task.edit(ps);
            ps.execute();
            task.onSuccess();
        } catch (SQLException e) {
            task.onFailed(e);
        }
    }

    interface Task {
        void edit(PreparedStatement ps) throws SQLException;

        default void onSuccess() {
        }

        default void onFailed(SQLException e) {
            e.printStackTrace();
        }

    }

}
