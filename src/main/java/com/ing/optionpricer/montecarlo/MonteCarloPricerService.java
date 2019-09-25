package com.ing.optionpricer.montecarlo;

import com.ing.optionpricer.model.OptionTrade;
import com.ing.optionpricer.model.OptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class MonteCarloPricerService {
    private static final Logger log = LoggerFactory.getLogger(MonteCarloPricerService.class);

    public Double priceOptionTradeSingleThreaded(OptionTrade optionTrade, int numberOfSimulations, int steps) {
        double sum = 0;
        for(int i=0;i<numberOfSimulations;i++) {
            sum += generateOptionPrice(optionTrade, steps);
        }

        return sum/numberOfSimulations * Math.exp(-optionTrade.getRiskRate() * optionTrade.getMaturityDate());
    }

    public Double priceOptionTrade(OptionTrade optionTrade, int numberOfSimulations, int steps) throws InterruptedException, ExecutionException {
        List<Callable<Double>> taskList = new ArrayList<>();
        for(int i=0;i<numberOfSimulations;i++) {
            taskList.add(() -> generateOptionPrice(optionTrade, steps));
        }

        int numberOfCores = Runtime.getRuntime().availableProcessors();

//        log.info("Pricing tradeId {}", optionTrade.getTradeId());
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfCores);
        List<Future<Double>> resultList = executorService.invokeAll(taskList);
        executorService.shutdown();

        double sum = 0.0;
        for(Future<Double> result : resultList) {
            sum += result.get();
        }

//        log.info("Finished pricing tradeId {}", optionTrade.getTradeId());
        return sum/numberOfSimulations * Math.exp(-optionTrade.getRiskRate() * optionTrade.getMaturityDate());
    }

    private double generateOptionPrice(OptionTrade optionTrade, int steps) {
        float currentPrice = optionTrade.getStockPrice();
        float deltaT = optionTrade.getMaturityDate()/steps;
        Random random = new Random();

        for(int i=0;i<steps;i++) {
            currentPrice *= Math.exp(
                    (optionTrade.getRiskRate() - 0.5f * optionTrade.getVolatility()) * deltaT
                    + Math.sqrt(optionTrade.getVolatility() * deltaT) * ((Double)random.nextGaussian()).floatValue());
        }

        if(optionTrade.getOptionType() == OptionType.CALL) {
            return Math.max(currentPrice - optionTrade.getStrikePrice(), 0);
        }

        return Math.max(optionTrade.getStrikePrice() - currentPrice, 0);
    }


}
