package com.app.ecommerce.core.product;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import com.app.ecommerce.core.product.dto.ProductRequest;

import com.app.ecommerce.core.product.dto.ProductResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void addProduct(ProductRequest request) {
        productRepository.findByName(request.getName()).ifPresent(product ->{
            throw new SystemServiceException(ExceptionMessages.PRODUCT_EXIST);
        });
        var product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .build();
        productRepository.save(product);
    }

    @Override
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .toList();
    }
}

