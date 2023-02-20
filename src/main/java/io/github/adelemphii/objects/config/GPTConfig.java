package io.github.adelemphii.objects.config;

import lombok.Getter;
import lombok.Setter;

public class GPTConfig {

    @Getter @Setter
    private String gptLanguage;
    @Getter @Setter
    private double gptTemperature;
    @Getter @Setter
    private int gptMaxTokens;

    public GPTConfig(String gptLanguage, double gptTemperature, int gptMaxTokens) {
        this.gptLanguage = gptLanguage;
        this.gptTemperature = gptTemperature;
        this.gptMaxTokens = gptMaxTokens;
    }

    public GPTConfig() {
        this.gptLanguage = "en";
        this.gptTemperature = 0.0;
        this.gptMaxTokens = 100;
    }
}
