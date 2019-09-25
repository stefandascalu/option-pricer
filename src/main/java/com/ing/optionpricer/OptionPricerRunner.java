package com.ing.optionpricer;

import com.ing.optionpricer.generator.OptionGenerator;
import com.ing.optionpricer.model.OptionTrade;
import com.ing.optionpricer.montecarlo.MonteCarloPricerService;
import com.ing.optionpricer.montecarlo.MonteCarloService;
import com.ing.optionpricer.montecarlo.gpu.GPUTradePricer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OptionPricerRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(OptionPricerRunner.class);

    private final MonteCarloPricerService monteCarloPricerService;
    private final GPUTradePricer GPUTradePricer;
    private final MonteCarloService monteCarloService;
    private final OptionGenerator optionGenerator;

    @Autowired
    public OptionPricerRunner(MonteCarloPricerService monteCarloPricerService,
                              GPUTradePricer GPUTradePricer,
                              MonteCarloService monteCarloService,
                              OptionGenerator optionGenerator) {
        this.monteCarloPricerService = monteCarloPricerService;
        this.GPUTradePricer = GPUTradePricer;
        this.monteCarloService = monteCarloService;
        this.optionGenerator = optionGenerator;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<OptionTrade> optionTrades = optionGenerator.generateOptionTrades();
        monteCarloService.priceOnCpu(optionTrades);
        monteCarloService.priceOnGpu(optionTrades);
    }
}
