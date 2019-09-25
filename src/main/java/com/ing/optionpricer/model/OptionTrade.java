package com.ing.optionpricer.model;

public class OptionTrade {
    private String tradeId;
    private float stockPrice;
    private float strikePrice;
    private float riskRate;
    private float volatility;
    private float maturityDate;
    private OptionStyle optionStyle;
    private OptionType optionType;

    public float getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(float stockPrice) {
        this.stockPrice = stockPrice;
    }

    public float getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(float strikePrice) {
        this.strikePrice = strikePrice;
    }

    public float getRiskRate() {
        return riskRate;
    }

    public void setRiskRate(float riskRate) {
        this.riskRate = riskRate;
    }

    public float getVolatility() {
        return volatility;
    }

    public void setVolatility(float volatility) {
        this.volatility = volatility;
    }

    public float getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(float maturityDate) {
        this.maturityDate = maturityDate;
    }

    public OptionStyle getOptionStyle() {
        return optionStyle;
    }

    public void setOptionStyle(OptionStyle optionStyle) {
        this.optionStyle = optionStyle;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
}
