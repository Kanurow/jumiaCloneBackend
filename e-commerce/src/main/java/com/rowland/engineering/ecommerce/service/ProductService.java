package com.rowland.engineering.ecommerce.service;

import com.rowland.engineering.ecommerce.dto.*;
import com.rowland.engineering.ecommerce.exception.BadRequestException;
import com.rowland.engineering.ecommerce.exception.InsufficientFundException;
import com.rowland.engineering.ecommerce.exception.ResourceNotFoundException;
import com.rowland.engineering.ecommerce.model.*;
import com.rowland.engineering.ecommerce.repository.*;
import com.rowland.engineering.ecommerce.utils.ProductImageUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FavouriteRepository favouriteRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartCheckoutRepository cartCheckoutRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


//    public Product createProduct(ProductRequest productRequest) {
//        Product product = new Product();
//        product.setProductName(productRequest.getProductName());
//        product.setPrice(BigDecimal.valueOf(productRequest.getPrice()));
//        product.setQuantity(productRequest.getQuantity());
//        return productRepository.save(product);
//    }


    public ApiResponse markProductAsFavourite(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        User user = userRepository.getReferenceById(userId);
        Favourite existingFavourite = favouriteRepository.findByProductAndUser(product, user);
        if (existingFavourite != null) {
            logger.info("{} has already been marked", product.getProductName());
            throw new BadRequestException("Sorry! You have already marked this product");
        }

        Favourite favourite = new Favourite();
        favourite.setProduct(product);
        favourite.setUser(user);

        try {
            favourite = favouriteRepository.save(favourite);
        } catch (DataIntegrityViolationException ex) {
            logger.info("{} has already been marked product", product.getProductName());
            throw new BadRequestException("Sorry! You have already marked this product");
        }
        return new ApiResponse(true, "Favourite Selected");
    }


    public ApiResponse addToCart(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        User user = userRepository.getReferenceById(userId);
        ShoppingCart existingCartEntry = shoppingCartRepository.findByProductAndUser(product, user);
        if (existingCartEntry != null) {
            logger.info("{} has already been added to your shopping list", product.getProductName());
            throw new BadRequestException("Sorry! You have already added this product");
        }
        ShoppingCart cart = new ShoppingCart();
        cart.setProduct(product);
        cart.setUser(user);

        shoppingCartRepository.save(cart);
        return new ApiResponse(true, "Product Added to cart");
    }


    public List<Product> viewProduct() {
        return productRepository.findAll();

    }

    public List<Favourite> viewMarked() {
        List<Favourite> fav = favouriteRepository.findAll();
        return fav;
    }

//    public ApiResponse deleteProduct(Long id, User currentUser) {
//        List<Favourite> productIds = favouriteRepository.findAllByProductId(id);
//        favouriteRepository.deleteAll(productIds);
//        shoppingCartRepository.deleteByProductId(id);
//        productRepository.deleteById(id);
//        return new ApiResponse(true, "Product with id "+id+ " Deleted by user with id "+ currentUser.getId());
//    }

    public ApiResponse deleteProduct(Long id, User currentUser) {
        List<Favourite> productIds = favouriteRepository.findAllByProductId(id);
        favouriteRepository.deleteAll(productIds);

        // Delete the product from the shopping cart for the current user
        ShoppingCart shoppingCart = shoppingCartRepository.findByProductIdAndUserId(id, currentUser.getId());
        if (shoppingCart != null) {
            shoppingCartRepository.delete(shoppingCart);
        }

        productRepository.deleteById(id);
        return new ApiResponse(true, "Product with id " + id + " deleted by user with id " + currentUser.getId());
    }


    public ApiResponse unmark(Long id, User currentUser) throws Exception {
        try {
            favouriteRepository.deleteById(id);
            return new ApiResponse(true, "Favourite Unmarked by user with id " + currentUser.getFirstName());
        } catch (Exception ex) {
            throw new Exception("Error occurred while unmarking the favourite: " + ex.getMessage(), ex);
        }
    }


    public PromoCode createPromo(PromoCodeRequest promoCodeRequest) {
        PromoCode promo = new PromoCode();
        promo.setCode(promoCodeRequest.getCode());
        promo.setPromoAmount(promoCodeRequest.getPromoAmount());
        return promoCodeRepository.save(promo);
    }

    public List<PromoCode> getAllPromo() {
        return promoCodeRepository.findAll();
    }

    public List<Favourite> getUserFavourites(Long userId) {
        return favouriteRepository.findAllFavouriteByUserId(userId);
    }

    public List<ShoppingCart> getUserCart(Long userId) {
        return shoppingCartRepository.findAllByUserId(userId);
    }

    public ApiResponse removeFromCart(Long id) {
        shoppingCartRepository.deleteById(id);
        return new ApiResponse(true, "Item removed from cart");
    }


    public ApiResponse checkoutCart(CartCheckoutRequest checkoutRequest, Long userId) {
        User user = userRepository.getReferenceById(userId);
        User newUser = new User(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getMobile(), user.getDateOfBirth(), user.getAccountNumber(), user.getRoles(), (List<GrantedAuthority>) user.getAuthorities());

        if (checkoutRequest.getTotal() > user.getAccountBalance()) {
            throw new InsufficientFundException(checkoutRequest.getTotal() - user.getAccountBalance());
        } else {
            newUser.setAccountBalance(user.getAccountBalance() - checkoutRequest.getTotal());
        }


        CartCheckout cartCheckout = new CartCheckout();
        cartCheckout.setOrderAddress(checkoutRequest.getOrderAddress());
        cartCheckout.setPrice(checkoutRequest.getTotal());
        cartCheckout.setQuantity(checkoutRequest.getQuantity());
        cartCheckout.setUserId(userId);

        // Create CartItem entities and populate the cart list
        List<CartCheckout.CartItem> cartItems = checkoutRequest.getCart().stream()
                .map(item -> {
                    CartCheckout.CartItem cartItem = new CartCheckout.CartItem();
                    cartItem.setProductName(item.getProductName());
                    cartItem.setPrice(item.getPrice());
                    cartItem.setQuantity(item.getQuantity());
                    cartItem.setSubtotal(item.getSubtotal());
                    return cartItem;
                })
                .collect(Collectors.toList());
        cartCheckout.setCart(cartItems);

        // Save the CartCheckout entity to the database
        cartCheckoutRepository.save(cartCheckout);
        userRepository.save(newUser);

        return new ApiResponse(true, "Checked Out");
    }

    public List<CartCheckout> getCheckedOutCart(Long id) {

        return cartCheckoutRepository.findByUserId(id);
    }


//    public ResponseEntity<?> createProduct(MultipartFile imageFile, String productName, BigDecimal price, String category, Integer quantity, String description) throws IOException {
//        Product product = Product.builder()
//                .productName(productName)
//                .price(price)
//                .quantity(quantity)
//                .category(category)
//                .description(description)
//                .image(ProductImageUtils.compressImage(imageFile.getBytes()))
//                .build();
//        productRepository.save(product);
//        return ResponseEntity.status(HttpStatus.OK).body(product);
//    }


//    public String uploadProduct(MultipartFile file, ProductRequest productRequest) throws IOException {
//        Product product = productRepository.save(Product.builder()
//                .productName(productRequest.getProductName())
//                .price(productRequest.getPrice())
//                .description(productRequest.getDescription())
//                .image(ProductImageUtils.compressImage(file.getBytes()))
//                .build());
//        if (product != null) {
//            return "File uploaded successfully";
//        }
//        return "Failed";
//    }


    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(item -> {
            Product product = new Product();
            product.setId(item.getId());
            product.setPercentageDiscount(item.getPercentageDiscount());
            product.setSellingPrice(item.getSellingPrice());
            product.setAmountDiscounted(item.getAmountDiscounted());
            product.setProductName(item.getProductName());
            product.setQuantity(item.getQuantity());
            product.setCategory(item.getCategory());
            product.setDescription(item.getDescription());
            product.setImage(ProductImageUtils.decompressImage(item.getImage()));
            return product;
        }).collect(Collectors.toList());
    }

    public byte[] downloadProductImage(String productName) {
        Optional<Product> product = productRepository.findByProductName(productName);
        byte[] img = ProductImageUtils.decompressImage(product.get().getImage());
        return img;
    }


    public ResponseEntity<?> createProduct(MultipartFile imageFile, String productName, Double price, Integer percentageDiscount, String category, Integer quantity, String description) {
        try {
            byte[] compressedImage = ProductImageUtils.compressImage(imageFile.getBytes());
            Product product = new Product();
            Double productPrice;
            Double amountDiscountedFromOriginalPrice;
            if (percentageDiscount > 0) {
                amountDiscountedFromOriginalPrice = price * (percentageDiscount /  (double) 100);
                productPrice = price - amountDiscountedFromOriginalPrice;
                product.setAmountDiscounted(amountDiscountedFromOriginalPrice);
                product.setSellingPrice(productPrice);

            } else {
                productPrice = price;
                product.setSellingPrice(productPrice);
                product.setAmountDiscounted(0.0);
            }

            product.setProductName(productName);
            product.setPercentageDiscount(percentageDiscount);
            product.setQuantity(quantity);
            product.setCategory(category);
            product.setDescription(description);
            product.setImage(compressedImage);

            Product savedProduct = productRepository.save(product);

            return ResponseEntity.status(HttpStatus.OK).body(savedProduct.getProductName() + " has been created successfully under " +savedProduct.getCategory() + " category" );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process product upload.");
        }
    }
}
