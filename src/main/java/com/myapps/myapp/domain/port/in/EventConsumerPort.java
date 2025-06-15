package com.myapps.myapp.domain.port.in;

import com.myapps.myapp.domain.events.ProductDetailsChangedEvent;

public interface EventConsumerPort {

    /**
     * Changes the product details in the cache based on the provided product
     * details changed event.
     *
     * @param event The product details changed event to be consumed.
     */
    void consumeProductDetailsChangedEvent(ProductDetailsChangedEvent event);

}
