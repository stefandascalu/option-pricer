package com.ing.optionpricer.montecarlo.gpu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GPUTradePricer {
    private static final Logger log = LoggerFactory.getLogger(GPUTradePricer.class);

    public float[] priceEuroCallOption(GPUOptionTrade gpuOptionTrade, int iterations, float[] rnd, boolean printGPUInfo) {
        MonteCarloOptionKernel monteCarloOptionKernel = new MonteCarloOptionKernel(gpuOptionTrade, iterations, rnd);
        return runKernel(gpuOptionTrade.getStockPrices(), iterations, monteCarloOptionKernel, printGPUInfo);
    }

    private float[] runKernel(float[] stockPrices, int iterations, MonteCarloOptionKernel kernel, boolean printGPUInfo) {
       //TODO Call the actual kernel and price the trades
    }
}
