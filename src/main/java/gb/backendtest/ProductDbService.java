package gb.backendtest;

import gb.backendtest.dao.ProductsMapper;
import gb.backendtest.model.Products;
import gb.backendtest.model.ProductsExample;

public class ProductDbService {
    ProductsMapper productsMapper;

    public ProductDbService() {
        productsMapper = DbUtils.getProductsMapper();
    }

    public void insertProduct(Products product) {
        productsMapper.insert(product);
    }

    public void  updateProduct(Products product){
        productsMapper.updateByPrimaryKey(product);
    }

    public void deleteProduct(Products product) {
        deleteProduct(product.getId());
    }

    public void deleteProduct(long id) {
        productsMapper.deleteByPrimaryKey(id);
    }

    public int countProducts(ProductsExample example) {
        long products = productsMapper.countByExample(example);
        return Math.toIntExact(products);
    }

    public Products getProductByPrimaryKey(long id) {
        return productsMapper.selectByPrimaryKey(id);
    }
}
