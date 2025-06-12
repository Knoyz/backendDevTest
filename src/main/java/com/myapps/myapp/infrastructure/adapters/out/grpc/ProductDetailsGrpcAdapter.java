package com.myapps.myapp.infrastructure.adapters.out.grpc;

// import java.math.BigDecimal;

// import org.springframework.stereotype.Component;

// import com.example.product.grpc.ExternalProductServiceGrpc;
// import com.example.product.grpc.ProductDetailsRequest;
// import com.example.product.grpc.ProductDetailsResponse;
// import com.myapps.myapp.domain.model.ProductDetails;
// import com.myapps.myapp.domain.port.out.ProductDetailsByIdPort;

// import io.grpc.ManagedChannel;
// import io.grpc.ManagedChannelBuilder;
// import reactor.core.publisher.Mono;

// @Component
// public class ProductDetailsGrpcAdapter implements ProductDetailsByIdPort {

// private final ExternalProductServiceGrpc.ExternalProductServiceBlockingStub
// blockingStub;

// public ProductDetailsGrpcAdapter() {
// ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 3001)
// .usePlaintext()
// .build();
// this.blockingStub = ExternalProductServiceGrpc.newBlockingStub(channel);
// }

// @Override
// public Mono<ProductDetails> getProductDetailsById(String productId) {
// try {
// ProductDetailsRequest request = ProductDetailsRequest.newBuilder()
// .setProductId(productId)
// .build();
// ProductDetailsResponse response = blockingStub.getProductDetails(request);
// return Mono.just(new ProductDetails(
// response.getId(),
// response.getName(),
// BigDecimal.valueOf(response.getPrice()),
// response.getAvailability()));
// } catch (Exception e) {
// return Mono.error(new RuntimeException("Error fetching product details via
// gRPC", e));
// }
// }
// }