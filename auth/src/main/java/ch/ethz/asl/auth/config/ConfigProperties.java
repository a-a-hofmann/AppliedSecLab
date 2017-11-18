package ch.ethz.asl.auth.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class ConfigProperties implements InitializingBean {

    private static ConfigProperties INSTANCE;

    private String flag;

    private String flagValue;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getFlagValue() {
        return flagValue;
    }

    public void setFlagValue(String flagValue) {
        this.flagValue = flagValue;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }

    public static ConfigProperties get() {
        return INSTANCE;
    }
}
