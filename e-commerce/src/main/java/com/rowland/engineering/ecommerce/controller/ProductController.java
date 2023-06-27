package com.rowland.engineering.ecommerce.controller;


import com.rowland.engineering.ecommerce.dto.*;
import com.rowland.engineering.ecommerce.model.*;
import com.rowland.engineering.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Product")
public class ProductController {
    private final ProductService productService;









    @Operation(
            description = "Get request for viewing all products",
            summary = "Returns list of all created products"
    )
    @GetMapping("/view")
    public List<Product> viewProducts() {
        return productService.viewProduct();
    }







    @Operation(
            description = "Get request for retrieving products in user shopping cart",
            summary = "Returns list of user cart items"
    )
    @GetMapping("/cart/{userId}")
    public List<ShoppingCart> getUserCart(
            @PathVariable(value = "userId") Long userId
    ) {
        return productService.getUserCart(userId);
    }







    @Operation(
            description = "Post request for checking out selected products",
            summary = "Checking out shopping cart"
    )
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<ApiResponse> checkoutCart(
            @RequestBody CartCheckoutRequest checkoutRequest,
            @PathVariable(value = "userId") Long userId) {
        return new ResponseEntity<ApiResponse>(productService.checkoutCart(checkoutRequest,userId),HttpStatus.ACCEPTED);
    }




    @Operation(
            description = "Returns list of all checked out product",
            summary = "Retrieves all checked out goods"
    )
    @GetMapping("/checkedout/{id}")
    public List<CartCheckout> getUserCartsByUserId(@PathVariable(value = "id") Long id) {
        return productService.getCheckedOutCart(id);
    }







//
//    @PostMapping
//    public ResponseEntity<String> createProductWithImg(@RequestParam("imageFile") MultipartFile imageFile,
//                                                @RequestParam("productName") String productName,
//                                                @RequestParam("price") BigDecimal price,
//                                                @RequestParam("quantity") Integer quantity,
//                                                @RequestParam("description") String description) {
//        try {
//            Product product = Product.builder()
//                    .productName(productName)
//                    .price(price)
//                    .quantity(quantity)
//                    .description(description)
//                    .build();
//
//            product.setImageFile(imageFile);
//
//            productService.createProduct(product);
//
//            return ResponseEntity.ok("Product created successfully");
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to process image file");
//        }
//    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            description = "Post request for product creation",
            summary = "Enables authorized users with admin role to create products"
    )
    @PostMapping("/createProduct")
    public ResponseEntity<String> createProductWithImg(@RequestParam("imageFile") MultipartFile imageFile,
                                                @RequestParam("productName") String productName,
                                                @RequestParam("price") Double price,
                                                @RequestParam("percentageDiscount") String percentageDiscount,
                                                @RequestParam("category") Category category,
                                                @RequestParam("quantity") Integer quantity,
                                                @RequestParam("description") String description) throws IOException {
        return productService.createProduct(imageFile,  productName,  price, Integer.valueOf(percentageDiscount),  category, quantity, description);

    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        List<ProductResponse> allProducts = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(allProducts);
    }

    @GetMapping("/supermarket")
    public ResponseEntity<List<ProductResponse>> getAllSupermarketProducts() {

        List<ProductResponse> allProducts = productService.getAllSupermarketProducts();
        return ResponseEntity.status(HttpStatus.OK).body(allProducts);
    }


    @Operation(
            description = "Post request for marking a product as favourite",
            summary = "Enables selecting a product as favourite"
    )
    @PostMapping("/addtocart/{productId}/{userId}")
    public ResponseEntity<ApiResponse> addToCart(
            @PathVariable(value = "productId") Long productId,
            @PathVariable(value = "userId") Long userId) {
        return new ResponseEntity<ApiResponse>(productService.addToCart(productId,userId),HttpStatus.ACCEPTED);
    }


    @Operation(
            description = "Delete request for removing a product from cart",
            summary = "Enables removing a product from cart"
    )
    @DeleteMapping("/removefromcart/{id}")
    public ResponseEntity<ApiResponse> removeFromCart(
            @PathVariable(value = "id") Long id) {
        return new ResponseEntity<ApiResponse>(productService.removeFromCart(id),HttpStatus.ACCEPTED);
    }


    @GetMapping("/orderHistory/{userId}")
    public ResponseEntity<List<OrderHistoryResponse>> getUserOrderHistory(
            @PathVariable(value = "userId") Long id
    ) {

        List<OrderHistoryResponse> orders = productService.getUserOrderHistory(id);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

}
