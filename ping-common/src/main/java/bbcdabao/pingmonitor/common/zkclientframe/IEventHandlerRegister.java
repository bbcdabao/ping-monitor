package bbcdabao.pingmonitor.common.zkclientframe;

/**
 * For register BaseEventHandler
 */
public interface IEventHandlerRegister {
    void registerEventHandler(String path, BaseEventHandler handler);
}
