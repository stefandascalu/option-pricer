package com.ing.optionpricer.montecarlo.gpu;

import com.aparapi.Kernel;

public class MonteCarloOptionKernel extends Kernel {
    private final float[] stockPrices;
    private final float[] strikePrices;
    private final float[] volatilites;
    private final float riskRate;
    private final  float maturityDate;
    private final int iterations;

    private final float[] gaussianRandom;
    private final float[] prices;
    private float[] result;

    private int operation = 0;

    private final int steps = 100;


    public MonteCarloOptionKernel(GPUOptionTrade gpuOptionTrade, int iterations, float[] gaussianRandom) {
        this.stockPrices = gpuOptionTrade.getStockPrices();
        this.strikePrices = gpuOptionTrade.getStrikePrices();
        this.volatilites = gpuOptionTrade.getVolatilites();
        this.riskRate = gpuOptionTrade.getRiskRate();
        this.maturityDate = gpuOptionTrade.getMaturityDate();
        this.iterations = iterations;
        this.gaussianRandom = gaussianRandom;

        this.prices = new float[iterations * this.stockPrices.length];
        this.result = new float[this.stockPrices.length];
    }

    @Override
    public void run() {
        int mcIter = getGlobalId();
        int stockPricesIndex = mcIter / iterations;
        if (operation == 0) {
            calculateMonteCarlo(mcIter, stockPricesIndex, steps);
        }

        if(operation == 1) {
            aggregate(mcIter);
        }
    }

    public void calculateMonteCarlo(int iteration, int index, int steps) {
        float volatility = this.volatilites[index];
        float stockPrice = this.stockPrices[index];
        float strikePrice = this.strikePrices[index];
        float deltaT = maturityDate/steps;
        float sum = stockPrice;
        for(int i=0;i<steps;i++) {
            float eps = gaussianRandom[i * iterations + iteration % iterations];
            sum = sum * exp(
                    (this.riskRate - 0.5f * volatility) * deltaT
                            + sqrt(volatility * deltaT) * eps);
        }
        prices[iteration] = max(sum - strikePrice, 0.0f);
    }

    public void aggregate(int stockIdx) {
        float sum = 0;
        for(int j=0;j<iterations;j++) {
            int index = stockIdx * iterations + j;
            sum += prices[index];
        }
        result[stockIdx] = (sum / iterations) * exp(-riskRate * maturityDate);
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public float[] getResult() {
        return result;
    }
}
