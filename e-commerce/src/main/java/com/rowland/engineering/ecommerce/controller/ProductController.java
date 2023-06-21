package com.rowland.engineering.ecommerce.controller;


import com.rowland.engineering.ecommerce.dto.*;
import com.rowland.engineering.ecommerce.model.*;
import com.rowland.engineering.ecommerce.security.CurrentUser;
import com.rowland.engineering.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Product")
public class ProductController {
    private final ProductService productService;


//    @Operation(
//            description = "Post request for product creation",
//            summary = "Enables authorized users with admin role to create products"
//    )
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest productRequest) {
//        Product product = productService.createProduct(productRequest);
//
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentRequest().path("/{productId}")
//                .buildAndExpand(product.getId()).toUri();
//        return ResponseEntity.created(location)
//                .body(new ApiResponse(true, "Product Created Successfully"));
//    }


//    @Operation(
//            description = "Post request for selecting a product",
//            summary = "Enables selecting a product as favourite"
//    )
//    @PostMapping("/mark/{productId}/{userId}")
//    public ResponseEntity<ApiResponse> markProductAsFavourite(
//            @PathVariable(value = "productId") Long productId,
//            @PathVariable(value = "userId") Long userId) {
//        return new ResponseEntity<ApiResponse>(productService.markProductAsFavourite(productId,userId),HttpStatus.ACCEPTED);
//    }

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
    @DeleteMapping("/unmark/{id}")
    public ResponseEntity<ApiResponse> unmarkProductAsFavourite(
            @PathVariable(value = "id") Long id,
            @CurrentUser User currentUser) {
        try {
            return new ResponseEntity<>(productService.unmark(id, currentUser), HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ApiResponse(false, "Error occurred while unmarking the favourite: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Operation(
            description = "Get request for viewing all products",
            summary = "Returns list of all created products"
    )
    @GetMapping("/view")
    public List<Product> viewProducts() {
        return productService.viewProduct();
    }


    @Operation(
            description = "Get request for all selected products",
            summary = "Returns list of all selected products by all users"
    )
    @GetMapping("/favourites")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Favourite> viewAllMarked() {
        return productService.viewMarked();
    }



    @Operation(
            description = "Get request for all selected products by user",
            summary = "Returns list of all marked products by logged in user"
    )
    @GetMapping("/favourites/{userId}")
    public List<Favourite> getUserMarkedFavourites(
            @PathVariable(value = "userId") Long userId
    ) {
        return productService.getUserFavourites(userId);
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
            description = "Delete selected product",
            summary = "Deletes created product"
    )
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(
            @PathVariable(value = "id") Long id,
            @CurrentUser User currentUser) {
        return new ResponseEntity<ApiResponse>(productService.deleteProduct(id, currentUser),HttpStatus.OK);
    }


    @Operation(
            description = "Creates promo code",
            summary = "Enables admin create special codes when used during user registration, will earn free voucher"
    )
    @PostMapping("/addPromoCode")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createPromo(@Valid @RequestBody PromoCodeRequest promoCodeRequest) {
        PromoCode promo = productService.createPromo(promoCodeRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{promoId}")
                .buildAndExpand(promo.getId()).toUri();
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Promo Created Successfully"));
    }

    @Operation(
            description = "Get all created promo code",
            summary = "Enables admin view all created promo codes"
    )
    @GetMapping("/getPromoCodes")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<PromoCode> getAllPromo() {
        return productService.getAllPromo();
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
    public ResponseEntity<?> createProductWithImg(@RequestParam("imageFile") MultipartFile imageFile,
                                                @RequestParam("productName") String productName,
                                                @RequestParam("price") Double price,
                                                @RequestParam("percentageDiscount") String percentageDiscount,
                                                @RequestParam("category") String category,
                                                @RequestParam("quantity") Integer quantity,
                                                @RequestParam("description") String description) throws IOException {
        return productService.createProduct(imageFile,  productName,  price, Integer.valueOf(percentageDiscount),  category, quantity, description);

    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {

        List<Product> allProducts = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(allProducts);
    }

    @GetMapping("/product/{productName}")
    public ResponseEntity<?> downloadImage(@PathVariable String productName) {

        byte[] img = productService.downloadProductImage(productName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/*"))
                .body(img);
    }



}
