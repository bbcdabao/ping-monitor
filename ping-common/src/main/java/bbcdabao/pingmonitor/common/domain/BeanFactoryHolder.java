/**
 * Copyright 2025 bbcdabao Team
 */

package bbcdabao.pingmonitor.common.domain;

import java.util.ServiceLoader;

public class BeanFactoryHolder {
    
    private static class Holder {
        private static final IBeanFactory INSTANCE = getBeanFactory();
    }
    
    private static IBeanFactory getBeanFactory() {
        ServiceLoader<IBeanFactory> loader = ServiceLoader.load(IBeanFactory.class);
        return loader.findFirst().get();
    }

    public static IBeanFactory getInstance() {
        return Holder.INSTANCE;
    }
}
