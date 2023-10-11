package com.mirea

import org.springframework.stereotype.Controller

@Controller
class StockController {

    @Autowired
    StockService stockService

    def index() {
        return [stocks: stockService.findAllStocks()]
    }

    def search() {
        def companyName = params.companyName
        def matchingStocks = stockService.findStocksByCompanyName(companyName)
        return [stocks: matchingStocks]
    }
}
