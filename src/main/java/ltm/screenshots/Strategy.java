package ltm.screenshots;

import ltm.exceptions.TestManagerException;

public enum Strategy {
    ON_EACH_STEP,
    ON_EACH_SCENARIO,
    ON_FAILURE,
    DISABLED;

    public static Strategy getValue(String strategy) {
        try {
            return valueOf(strategy);
        } catch (IllegalArgumentException e) {
            throw new TestManagerException("test-manager.screenshots property only supports: [ON_AFTER_STEP, ON_AFTER_SCENARIO, OR ON_FAILURE] strategies");
        }
    }
}