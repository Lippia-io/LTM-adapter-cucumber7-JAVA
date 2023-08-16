package ltm.screenshots;

import ltm.PropertyManager;

import org.springframework.util.StringUtils;

public class SSConfig {
    protected SSConfig(Strategy strategy) {
        this.strategy = strategy;
    }

    public static SSConfig load() {
        String strategy = PropertyManager.getProperty("test-manager.screenshots");
        if (StringUtils.isEmpty(strategy)) {
            return new SSConfig(Strategy.DISABLED);
        }

        return new SSConfig(Strategy.getValue(strategy));
    }

    public boolean contains(Strategy strategy) {
        return this.strategy == strategy;
    }

    public boolean isNotContains(Strategy strategy) {
        return !(contains(strategy));
    }

    private final Strategy strategy;
}