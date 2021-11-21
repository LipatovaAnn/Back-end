package gb.backendtest;

import com.github.javafaker.Faker;
import gb.backendtest.model.Products;
import gb.backendtest.model.ProductsExample;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;


public class ProductTests {
    static ProductService productService;
    Product product;
    Faker faker = new Faker();

    long id;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));
    }

    private ProductDbService getService() {
        return new ProductDbService();
    }

    @Test
    @SneakyThrows
    void createProductTest() {
        int count = getService().countProducts(new ProductsExample());

        Response<Product> response = productService.createProduct(product.withId(null))
                .execute();
        id = response.body().getId();
        MatcherAssert.assertThat(response.isSuccessful(), CoreMatchers.is(true));

        int newCount = getService().countProducts(new ProductsExample());
        System.out.println(count);
        System.out.println(newCount);
        boolean countIncreased = (newCount - count) > 0;
        MatcherAssert.assertThat(countIncreased, CoreMatchers.is(true));
    }


    @Test
    @SneakyThrows
    void createProductWithIdTest() {
        Response<Product> response = productService.createProduct(product.withId(123l))
                .execute();
        MatcherAssert.assertThat(response.code(), CoreMatchers.is(400));
        id = 0;
    }

    @Test
    @SneakyThrows
    void getProductWithIdTest() {
        Response<Product> response = productService.createProduct(product)
                .execute();
        id = response.body().getId();
        MatcherAssert.assertThat(response.isSuccessful(), CoreMatchers.is(true));

        response = productService.getProduct(id)
                .execute();
        MatcherAssert.assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

    @Test
    @SneakyThrows
    void getProductWithoutIdTest() {
        id = 0;
        Response<Product> response = productService.getProduct(-1)
                .execute();
        MatcherAssert.assertThat(response.code(), CoreMatchers.is(404));
    }

    @Test
    @SneakyThrows
    void updateProductTest() {
        Response<Product> response = productService.createProduct(product)
                .execute();
        id = response.body().getId();
        MatcherAssert.assertThat(response.isSuccessful(), CoreMatchers.is(true));

        product = product.withId(id);
        String newTitle = "Update title";
        response = productService.updateProduct(product.withTitle(newTitle))
                .execute();
        MatcherAssert.assertThat(response.isSuccessful(), CoreMatchers.is(true));

        Products p = getService().getProductByPrimaryKey(id);
        MatcherAssert.assertThat(p.getTitle(), CoreMatchers.is(newTitle));
    }

    @Test
    @SneakyThrows
    void updateProductWithoutIdTest() {
        Response<Product> response = productService.updateProduct(product.withId(null))
                .execute();
        MatcherAssert.assertThat(response.code(), CoreMatchers.is(400));
        id = 0;
    }

    @Test
    @SneakyThrows
    void updateProductWithoutRequiredFieldsTest() {
        Response<Product> response = productService.createProduct(product)
                .execute();
        id = response.body().getId();
        MatcherAssert.assertThat(response.isSuccessful(), CoreMatchers.is(true));

        product = product.withId(id);

        response = productService.updateProduct(product.withTitle(null))
                .execute();
        MatcherAssert.assertThat("Title is required", response.code(), CoreMatchers.is(400));

        response = productService.updateProduct(product.withCategoryTitle(null))
                .execute();
        MatcherAssert.assertThat("Category is required", response.code(), CoreMatchers.is(400));

        response = productService.updateProduct(product.withPrice(null))
                .execute();
        MatcherAssert.assertThat("Price is required", response.code(), CoreMatchers.is(400));
    }

    @Test
    @SneakyThrows
    void getAllProductsTest() {
        int count = getService().countProducts(new ProductsExample());
        MatcherAssert.assertThat("Returned any products", count, CoreMatchers.not(0));
        id = 0;
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        if (id > 0) {
            getService().deleteProduct(id);
        }
    }
}
