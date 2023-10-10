package com.mirea

import org.springframework.stereotype.Controller

@Controller
public class StockController {

    @Autowired
    StockService stockService
    
    def index() {
        return [stocks: stockService.findAllStocks()]
    }
}
