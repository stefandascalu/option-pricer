package com.ing.optionpricer.generator;

import com.ing.optionpricer.model.OptionStyle;
import com.ing.optionpricer.model.OptionTrade;
import com.ing.optionpricer.model.OptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class OptionGenerator {
    private static final Logger log = LoggerFactory.getLogger(OptionGenerator.class);

    private static final float[] DEFAULT_STOCK_PRICES = {45.0f, 55.0f, 65.0f, 75.0f, 85.0f, 95.0f};
    private static final float[] DEFAULT_STRIKE_PRICES = {50.0f, 60.0f, 70.0f, 80.0f, 90.0f, 100.0f};
    private static final float[] DEFAULT_VOLATILITIES = {0.02f, 0.03f, 0.04f, 0.05f, 0.06f, 0.07f};
    private static final float DEFAULT_MATURITY= 0.5f;
    private static final float DEFAULT_RISK_RATE = 0.8f;

    public List<OptionTrade> generateOptionTrades() {
        List<OptionTrade> optionTrades = new ArrayList<>();

        IntStream.range(0, 10).forEach(i -> optionTrades.add(makeOptionTrade(i)));

        return optionTrades;
    }

    private OptionTrade makeOptionTrade(int i) {
        Random random = new Random();
        OptionTrade optionTrade = new OptionTrade();

        optionTrade.setTradeId("TRADE_" + i);
        optionTrade.setStockPrice(generateRandomFloatInVectorInterval(DEFAULT_STOCK_PRICES));
        optionTrade.setStrikePrice(generateRandomFloatInVectorInterval(DEFAULT_STRIKE_PRICES));
        optionTrade.setVolatility(generateRandomFloatInVectorInterval(DEFAULT_VOLATILITIES));
        optionTrade.setMaturityDate(DEFAULT_MATURITY);
        optionTrade.setRiskRate(DEFAULT_RISK_RATE);
        optionTrade.setOptionType(OptionType.CALL);
        optionTrade.setOptionStyle(OptionStyle.EUROPEAN);

        return optionTrade;
    }

    private float generateRandomFloatInVectorInterval(float[] vector) {
        Random random = new Random();
        return vector[0] + random.nextFloat() * (vector[vector.length - 1] - vector[0]);
    }
}
