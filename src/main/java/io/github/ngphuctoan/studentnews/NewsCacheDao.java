package io.github.ngphuctoan.studentnews;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface NewsCacheDao {
    @SqlQuery("SELECT `id` FROM `news`")
    @RegisterConstructorMapper(News.class)
    List<Integer> listAllIds();

    @SqlBatch("INSERT INTO `news` (`id`, `title`, `summary`, `content`) VALUES (:id, :title, :summary, :content)")
    void insertMany(@BindMethods List<News> news);
}
