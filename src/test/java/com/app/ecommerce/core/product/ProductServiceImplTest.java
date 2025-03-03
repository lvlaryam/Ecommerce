package com.app.ecommerce.core.product;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.core.product.dto.ProductRequest;
import com.app.ecommerce.core.product.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void addProduct_ShouldSaveProduct_WhenProductDoesNotExist() {
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setPrice(1200.0);

        when(productRepository.findByName(request.getName())).thenReturn(Optional.empty());

        productService.addProduct(request);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void addProduct_ShouldThrowException_WhenProductAlreadyExists() {
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setPrice(1200.0);

        when(productRepository.findByName(request.getName()))
                .thenReturn(Optional.of(new Product()));

        assertThrows(SystemServiceException.class, () -> productService.addProduct(request));
    }

    @Test
    void getProducts_ShouldReturnMappedProductResponses() {
        List<Product> products = List.of(new Product(1L, "Laptop", 1200.0));
        when(productRepository.findAll()).thenReturn(products);
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class)))
                .thenReturn(new ProductResponse());

        List<ProductResponse> responses = productService.getProducts();

        assertEquals(1, responses.size());
        verify(productRepository, times(1)).findAll();
    }
}

