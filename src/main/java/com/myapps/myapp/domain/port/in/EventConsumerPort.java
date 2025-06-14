package com.myapps.myapp.domain.port.in;

import com.myapps.myapp.domain.events.ProductDetailsChangedEvent;

public interface EventConsumerPort {

    void consumeProductDetailsChangedEvent(ProductDetailsChangedEvent event);

}
