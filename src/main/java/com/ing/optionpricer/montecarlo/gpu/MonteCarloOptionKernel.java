package com.ing.optionpricer.montecarlo.gpu;

import com.aparapi.Kernel;

public class MonteCarloOptionKernel extends Kernel {
    private final float[] stockPrices;
    private final float[] strikePrices;
    private final float[] volatilites;
    private final float riskRate;
    private final  float maturityDate;
    private final int iterations;

    private final float[] rnd;
    private final float[] mcResults;
    private float[] result;

    private int operation = 0;

    private final int steps = 100;

    int i = 0;

    public MonteCarloOptionKernel(GPUOptionTrade gpuOptionTrade, int iterations, float[] rnd) {
        this.stockPrices = gpuOptionTrade.getStockPrices();
        this.strikePrices = gpuOptionTrade.getStrikePrices();
        this.volatilites = gpuOptionTrade.getVolatilites();
        this.riskRate = gpuOptionTrade.getRiskRate();
        this.maturityDate = gpuOptionTrade.getMaturityDate();
        this.iterations = iterations;
        this.rnd = rnd;

        this.mcResults = new float[iterations * this.stockPrices.length];
        this.result = new float[this.stockPrices.length];
    }

    @Override
    public void run() {
        //TODO 2. Create the run method of the kernel
    }

    public void calculateMonteCarlo(int mcIter, int stockIdx, int steps) {
        float volatility = this.volatilites[stockIdx];
        float stockPrice = this.stockPrices[stockIdx];
        float strikePrice = this.strikePrices[stockIdx];
        float deltaT = maturityDate/steps;
        float sum = stockPrice;
        for(int i=0;i<steps;i++) {
            float eps = rnd[i * iterations + mcIter % iterations];
            sum = sum * exp(
                    (this.riskRate - 0.5f * volatility) * deltaT
                            + sqrt(volatility * deltaT) * eps);
        }
        mcResults[mcIter] = max(sum - strikePrice, 0.0f);
    }

    public void aggregate(int stockIdx) {
        float sum = 0;
        for(int j=0;j<iterations;j++) {;
            int index = stockIdx * iterations + j;
            sum += mcResults[index];
        }
        float sTr = sum / iterations;
        result[stockIdx] = sTr * exp(-riskRate * maturityDate);
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public float[] getResult() {
        return result;
    }
}
