package ua.wyverno.twitch.api.authorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultAsk {

    private static final Logger logger = LoggerFactory.getLogger(ResultAsk.class);

    private final Object lockObjectAccess = new Object();
    private final Object lockObjectScope = new Object();
    private final Object lockObjectTypeToken = new Object();

    public ResultAsk() {}
    private volatile String accessToken;
    private volatile String scope;
    private volatile String tokenType;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        logger.debug("Set Access Token, so notify all threads");
        synchronized (this.lockObjectAccess) {
            this.lockObjectAccess.notifyAll();
        }
    }

    public void setScope(String scope) {
        this.scope = scope;
        logger.debug("Set scope, so notify all threads");
        synchronized (this.lockObjectScope) {
            this.lockObjectScope.notifyAll();
        }
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
        logger.debug("Set token type, so notify all threads");
        synchronized (this.lockObjectTypeToken) {
            this.lockObjectTypeToken.notifyAll();
        }
    }

    public String getAccessToken() throws Exception {
        synchronized (lockObjectAccess) {
            while (this.accessToken == null) {
                logger.debug("Access token = null, so Thread WAIT!");
                lockObjectAccess.wait();
            }
        }

        logger.debug("Return Access Token");
        return this.accessToken;
    }

    public String getScope() throws Exception {
        synchronized (lockObjectScope) {
            while (this.scope == null) {
                logger.debug("Scope = null, so Thread WAIT!");
                lockObjectScope.wait();
            }
        }

        logger.debug("Return Scope");
        return this.scope;
    }

    public String getTokenType() throws Exception {

        synchronized (lockObjectTypeToken) {
            while (this.tokenType == null) {
                logger.debug("Token Type = null, so Thread WAIT!");
                this.lockObjectTypeToken.wait();
            }
        }

        logger.debug("Return Token Type!");
        return tokenType;
    }

    @Override
    public String toString() {
        return "ResultAsk{" +
                "accessToken='" + accessToken + '\'' +
                ", scope='" + scope + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}
