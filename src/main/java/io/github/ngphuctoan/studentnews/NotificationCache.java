package io.github.ngphuctoan.studentnews;

import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface NotificationCache {
    @SqlQuery("SELECT `id` FROM `notifications`")
    List<Integer> listAllIds();

    @SqlBatch("INSERT INTO `notifications` (`id`, `title`, `summary`, `date_created`, `details`) VALUES (:id, :title, :summary, :dateCreated, :details)")
    int[] insertMany(@BindMethods List<Notification> notifications);
}
