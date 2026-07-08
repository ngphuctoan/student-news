package io.github.ngphuctoan;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface NewsDao {
    @SqlQuery("SELECT * FROM `news`")
    @RegisterConstructorMapper(News.class)
    List<News> listNews();

    @SqlUpdate("INSERT INTO `news` (`id`, `title`, `summary`, `content`) VALUES (:id, :title, :summary, :content)")
    void insertBean(@BindMethods News news);
}
