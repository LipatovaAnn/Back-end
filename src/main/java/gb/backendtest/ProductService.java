package gb.backendtest;

import retrofit2.Call;
import retrofit2.http.*;
import okhttp3.ResponseBody;


public interface ProductService {
    @GET("products")
    Call<Product[]> getProducts();

    @GET("products/{id}")
    Call<Product> getProduct(@Path("id") int id);

    @POST("products")
    Call<Product> createProduct(@Body Product createProductRequest);

    @PUT("products")
    Call<Product> updateProduct(@Body Product updateProductRequest);

    @DELETE("products/{id}")
    Call<ResponseBody> deleteProduct(@Path("id") int id);

}
