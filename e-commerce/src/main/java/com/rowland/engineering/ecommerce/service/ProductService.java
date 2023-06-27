package com.rowland.engineering.ecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rowland.engineering.ecommerce.dto.ApiResponse;
import com.rowland.engineering.ecommerce.dto.CartCheckoutRequest;
import com.rowland.engineering.ecommerce.dto.OrderHistoryResponse;
import com.rowland.engineering.ecommerce.dto.ProductResponse;
import com.rowland.engineering.ecommerce.exception.BadRequestException;
import com.rowland.engineering.ecommerce.exception.InsufficientFundException;
import com.rowland.engineering.ecommerce.exception.ResourceNotFoundException;
import com.rowland.engineering.ecommerce.model.*;
import com.rowland.engineering.ecommerce.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import java.util.Map;
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

    private Cloudinary cloudinary;

    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;



    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


    @PostConstruct
    public void initializeCloudinary() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }


    public ResponseEntity<String> createProduct(MultipartFile imageFile, String productName, Double price,
                                           Integer percentageDiscount, Category category, Integer quantity,
                                           String description)  {

        try {

            Map<?, ?> uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");

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
            product.setImageUrl(imageUrl);

            Product savedProduct = productRepository.save(product);

            return ResponseEntity.status(HttpStatus.OK).body(savedProduct.getProductName() + " has been created successfully under " +savedProduct.getCategory() + " category" );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process product upload.");
        }
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




    public List<ShoppingCart> getUserCart(Long userId) {
        return shoppingCartRepository.findAllByUserId(userId);
    }



    @Transactional
    public ApiResponse checkoutCart(CartCheckoutRequest checkoutRequest, Long userId) {
        System.out.println(checkoutRequest);
        User user = userRepository.getReferenceById(userId);

        User newUser = new User(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(),user.getDateOfBirth(), user.getEmail(), user.getPassword(), user.getMobile(), user.getJumiaAccountNumber(), user.getRoles(), (List<GrantedAuthority>) user.getAuthorities());

        if (checkoutRequest.getTotal() > user.getAccountBalance()) {
            throw new InsufficientFundException(checkoutRequest.getTotal() - user.getAccountBalance());
        } else {
            newUser.setAccountBalance(user.getAccountBalance() - checkoutRequest.getTotal());
        }


        CartCheckout cartCheckout = new CartCheckout();
        cartCheckout.setDeliveryAddress(checkoutRequest.getDeliveryAddress());
        cartCheckout.setFirstName(checkoutRequest.getFirstName());
        cartCheckout.setLastName(checkoutRequest.getLastName());
        cartCheckout.setPhoneNumber(checkoutRequest.getPhoneNumber());
        cartCheckout.setAlternativePhoneNumber(checkoutRequest.getAlternativePhoneNumber());
        cartCheckout.setAdditionalInformation(checkoutRequest.getAdditionalInformation());
        cartCheckout.setRegion(checkoutRequest.getRegion());
        cartCheckout.setState(checkoutRequest.getState());

        cartCheckout.setPrice(checkoutRequest.getTotal());
        cartCheckout.setQuantity(checkoutRequest.getQuantity());
//        cartCheckout.setTotal(checkoutRequest.getTotal());
        cartCheckout.setUserId(userId);

        List<CartCheckout.CartItem> cartItems = checkoutRequest.getCart().stream()
                .map(item -> {
                    CartCheckout.CartItem cartItem = new CartCheckout.CartItem();
                    cartItem.setProductId(item.getProductId());
                    cartItem.setProductName(item.getProductName());
                    cartItem.setPrice(item.getPrice());
                    cartItem.setImageUrl(item.getImageUrl());
                    cartItem.setQuantity(item.getQuantity());
                    cartItem.setSubtotal(item.getSubtotal());
                    return cartItem;
                })
                .collect(Collectors.toList());
        cartCheckout.setCart(cartItems);
        System.out.println(cartItems);

        List<Long> productIds =cartCheckout.getCart().stream().map(
                CartCheckout.CartItem::getProductId).collect(Collectors.toList());

        System.out.println(productIds);

        cartCheckoutRepository.save(cartCheckout);


        List<Long> shoppingCartIds = checkoutRequest.getCart().stream()
                .map(item -> {
                    return item.getProductId();
                })
                .collect(Collectors.toList());
        shoppingCartRepository.deleteAllById(shoppingCartIds);

        userRepository.save(newUser);

        return new ApiResponse(true, "Checked Out");
    }

    public List<CartCheckout> getCheckedOutCart(Long id) {
        return cartCheckoutRepository.findByUserId(id);
    }



    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(item -> {
            ProductResponse product = new ProductResponse();
            product.setId(item.getId());
            product.setPercentageDiscount(item.getPercentageDiscount());
            product.setSellingPrice(item.getSellingPrice());
            product.setAmountDiscounted(item.getAmountDiscounted());
            product.setProductName(item.getProductName());
            product.setQuantity(item.getQuantity());
            product.setCategory(item.getCategory());
            product.setDescription(item.getDescription());
            product.setImageUrl(item.getImageUrl());
            return product;
        }).collect(Collectors.toList());
    }

    public List<ProductResponse> getAllSupermarketProducts() {
        List<Product> supermarket = productRepository.findAllByCategory(Category.SUPERMARKET);
        return supermarket.stream().map(item -> {
            ProductResponse product = new ProductResponse();
            product.setId(item.getId());
            product.setPercentageDiscount(item.getPercentageDiscount());
            product.setSellingPrice(item.getSellingPrice());
            product.setAmountDiscounted(item.getAmountDiscounted());
            product.setProductName(item.getProductName());
            product.setQuantity(item.getQuantity());
            product.setCategory(item.getCategory());
            product.setDescription(item.getDescription());
            product.setImageUrl(item.getImageUrl());
            return product;
        }).collect(Collectors.toList());
    }

    public ApiResponse removeFromCart(Long id) {
        shoppingCartRepository.deleteById(id);
        return new ApiResponse(true, "Item removed from cart");
    }


    public List<OrderHistoryResponse> getUserOrderHistory(Long id) {
        List<CartCheckout> userCartItems = cartCheckoutRepository.findByUserId(id);
        return userCartItems.stream().map(item -> {
            OrderHistoryResponse order = new OrderHistoryResponse();
            order.setId(item.getId());
            order.setQuantity(item.getQuantity());
            order.setFirstName(item.getFirstName());
            order.setLastName(item.getLastName());
            order.setPhoneNumber(item.getPhoneNumber());
            order.setAlternativePhoneNumber(item.getAlternativePhoneNumber());
            order.setDeliveryAddress(item.getDeliveryAddress());
            order.setAdditionalInformation(item.getAdditionalInformation());
            order.setState(item.getState());
            order.setRegion(item.getRegion());

            List<OrderHistoryResponse.CartItem> cartItems = item.getCart().stream()
                    .map(cartItem -> {
                        OrderHistoryResponse.CartItem historyCartItem = new OrderHistoryResponse.CartItem();
                        historyCartItem.setProductId(cartItem.getProductId());
                        historyCartItem.setProductName(cartItem.getProductName());
                        historyCartItem.setPrice(cartItem.getPrice());
                        historyCartItem.setImageUrl(cartItem.getImageUrl());
                        historyCartItem.setQuantity(cartItem.getQuantity());
                        historyCartItem.setSubtotal(cartItem.getSubtotal());
                        return historyCartItem;
                    })
                    .collect(Collectors.toList());

            order.setCart(cartItems);
            System.out.println(order);

            return order;
        }).collect(Collectors.toList());
    }


}
