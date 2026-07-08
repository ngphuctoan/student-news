package io.github.ngphuctoan;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface NewsCacheDao {
    @SqlQuery("SELECT * FROM `news`")
    @RegisterConstructorMapper(News.class)
    List<News> listAll();

    @SqlBatch("INSERT INTO `news` (`id`, `title`, `summary`, `content`) VALUES (:id, :title, :summary, :content)")
    void insertMany(@BindMethods List<News> news);
}
