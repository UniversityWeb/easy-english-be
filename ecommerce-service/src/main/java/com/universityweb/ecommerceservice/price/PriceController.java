package com.universityweb.ecommerceservice.price;

import com.universityweb.price.request.PriceRequest;
import com.universityweb.price.response.PriceResponse;
import com.universityweb.price.service.PriceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/api/v1/price")
@RestController
@Tag(name = "Price")
public class PriceController {
    @Autowired
    private PriceService priceService;

    @PostMapping("/update-price")
    public String updatePrice(@RequestBody PriceRequest priceRequest) {
        priceService.updatePrice(priceRequest);
        return "Price updated successfully";
    }

    @PostMapping("/get-price-by-course")
    public ResponseEntity<PriceResponse> getPriceByCourse(@RequestBody PriceRequest priceRequest) {
        return ResponseEntity.ok(priceService.getPriceByCourse(priceRequest));
    }
}
