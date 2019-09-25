package com.ing.optionpricer.montecarlo.gpu;

public class GPUOptionTrade {
    private final float[] stockPrices;
    private final float[] strikePrices;
    private final float[] volatilites;
    private final float riskRate;
    private final float maturityDate;

    public GPUOptionTrade(float[] stockPrices, float[] strikePrices, float[] volatilites, float riskRate, float maturityDate) {
        this.stockPrices = stockPrices;
        this.strikePrices = strikePrices;
        this.volatilites = volatilites;
        this.riskRate = riskRate;
        this.maturityDate = maturityDate;
    }

    public float[] getStockPrices() {
        return stockPrices;
    }

    public float[] getStrikePrices() {
        return strikePrices;
    }

    public float[] getVolatilites() {
        return volatilites;
    }

    public float getRiskRate() {
        return riskRate;
    }

    public float getMaturityDate() {
        return maturityDate;
    }
}
