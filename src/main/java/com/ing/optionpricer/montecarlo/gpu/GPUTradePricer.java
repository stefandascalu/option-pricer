package com.ing.optionpricer.montecarlo.gpu;

import com.aparapi.Range;
import com.aparapi.device.Device;
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
        log.info("Running on GPU:" + kernel.isRunningCL());
        if(printGPUInfo) {
            log.info("Device Info:" + Device.best());
        }

        long mcStart = System.currentTimeMillis();
        kernel.execute(Range.create(stockPrices.length * iterations));
        long mcEnd = System.currentTimeMillis() - mcStart;


        kernel.setOperation(1);
        long aggregateStart = System.currentTimeMillis();
        kernel.execute(Range.create(stockPrices.length));
        long aggregateEnd = System.currentTimeMillis() - aggregateStart;

        kernel.dispose();
        log.info("Kernel stats MC took {}", (mcEnd + aggregateEnd)/1000.0);
        return kernel.getResult();
    }
}
