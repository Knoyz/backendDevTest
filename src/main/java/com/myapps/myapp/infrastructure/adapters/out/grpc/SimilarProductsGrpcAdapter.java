package com.myapps.myapp.infrastructure.adapters.out.grpc;

// import org.springframework.stereotype.Component;

// import com.example.product.grpc.ExternalProductServiceGrpc;
// import com.example.product.grpc.SimilarProductIdsRequest;
// import com.example.product.grpc.SimilarProductIdsResponse;
// import com.myapps.myapp.domain.port.out.SimilarProductsByIdPort;

// import io.grpc.ManagedChannel;
// import io.grpc.ManagedChannelBuilder;
// import reactor.core.publisher.Flux;

// @Component
// public class SimilarProductsGrpcAdapter implements SimilarProductsByIdPort {

// private final ExternalProductServiceGrpc.ExternalProductServiceBlockingStub
// blockingStub;

// public SimilarProductsGrpcAdapter() {
// ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 3001)
// .usePlaintext()
// .build();
// this.blockingStub = ExternalProductServiceGrpc.newBlockingStub(channel);
// }

// @Override
// public Flux<String> getSimilarProducts(String productId) {
// try {
// SimilarProductIdsRequest request = SimilarProductIdsRequest.newBuilder()
// .setProductId(productId)
// .build();
// SimilarProductIdsResponse response =
// blockingStub.getSimilarProductIds(request);
// return Flux.fromIterable(response.getProductIdsList());
// } catch (Exception e) {
// return Flux.error(new RuntimeException("Error fetching similar product IDs
// via gRPC", e));
// }
// }
// }