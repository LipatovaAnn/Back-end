package gb.backendtest;

import com.github.javafaker.Faker;
import gb.backendtest.dao.CategoriesMapper;
import gb.backendtest.dao.ProductsMapper;
import gb.backendtest.model.Categories;
import gb.backendtest.model.CategoriesExample;
import gb.backendtest.model.ProductsExample;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
@UtilityClass
public class DbUtils {
    private static  String resource = "mybatisConfig.xml";
    static Faker faker = new Faker();
    private static SqlSession getSqlSession() throws IOException {
        SqlSessionFactory sqlSessionFactory;
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(resource));
        return sqlSessionFactory.openSession(true);
    }
    @SneakyThrows
    public static CategoriesMapper getCategoriesMapper(){
        return getSqlSession().getMapper(CategoriesMapper.class);
    }
    @SneakyThrows
    public static ProductsMapper getProductsMapper() {
        return getSqlSession().getMapper(ProductsMapper.class);
    }
}
