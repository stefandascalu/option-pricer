package com.ing.optionpricer.montecarlo;

import com.ing.optionpricer.model.OptionTrade;
import com.ing.optionpricer.montecarlo.gpu.GPUOptionTrade;
import com.ing.optionpricer.montecarlo.gpu.GPUTradePricer;
import com.ing.optionpricer.montecarlo.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

@Service
public class MonteCarloService {
    private static final Logger log = LoggerFactory.getLogger(MonteCarloService.class);

    private final MonteCarloPricerService monteCarloPricerService;
    private final GPUTradePricer gpuTradePricer;

    @Autowired
    public MonteCarloService(MonteCarloPricerService monteCarloPricerService,
                             GPUTradePricer gpuTradePricer) {
        this.monteCarloPricerService = monteCarloPricerService;
        this.gpuTradePricer = gpuTradePricer;
    }

    public void priceOnCpu(List<OptionTrade> optionTradeList) {
        long startTime = System.currentTimeMillis();
        Double price = this.monteCarloPricerService.priceOptionTradeSingleThreaded(optionTradeList.get(0), 1_000_000, 100);
        long endTime = System.currentTimeMillis();

        log.info("Computation ST done in {} and result is {}", (endTime - startTime)/1000.0, price);


        startTime = System.currentTimeMillis();
        optionTradeList.parallelStream().forEach(optionTrade -> {
            try {
                System.out.println(this.monteCarloPricerService.priceOptionTrade(optionTrade, 1_000_000, 100));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        endTime = System.currentTimeMillis();

        log.info("Computation MT done in {}", (endTime - startTime)/1000.0);
    }

    public void priceOnGpu(List<OptionTrade> optionTrades) {
        GPUOptionTrade gpuOptionTrade = makeGpuOption(optionTrades);
        float[][] rnd = RandomUtils.get2dRandomArray(100_000, 100);
        float[] rnd1d = RandomUtils.to1dArray(rnd);

        float[] results = this.gpuTradePricer.priceEuroCallOption(gpuOptionTrade, 100_000,
                rnd1d, true);
//        for(int i=0;i<results.length;i++) {
//            System.out.println(results[i]);
//        }
    }

    private GPUOptionTrade makeGpuOption(List<OptionTrade> optionTradeList) {
        int size = optionTradeList.size();
        float[] stockPrices = new float[size];
        float[] strikePrices = new float[size];
        float[] volatilities = new float[size];
        IntStream.range(0, size).forEach(i -> {
            stockPrices[i] = optionTradeList.get(i).getStockPrice();
            strikePrices[i] = optionTradeList.get(i).getStrikePrice();
            volatilities[i] = optionTradeList.get(i).getVolatility();
        });

        return new GPUOptionTrade(stockPrices,
                strikePrices,
                volatilities,
                optionTradeList.get(0).getRiskRate(),
                optionTradeList.get(0).getMaturityDate());

    }


}
