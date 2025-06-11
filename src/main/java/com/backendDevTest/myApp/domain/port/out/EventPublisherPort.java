package com.backendDevTest.myApp.domain.port.out;

public interface EventPublisherPort {
    /**
     * Publishes an event to notify that similar products are available
     *
     * @param eventType The type of the event to be published, e.g.,
     *                  "similarProductsIds"
     * @param productId The unique identifier of the product for which similar
     *                  products are available
     */
    void publishEvent(String eventType, String productId);
}
