package bbcdabao.pingmonitor.common.zkclient;

/**
 * For register BaseEventHandler
 */
public interface IEventHandlerRegister {
    void registerEventHandler(String path, BaseEventHandler handler);
}
