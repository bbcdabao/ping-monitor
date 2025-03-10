package bbcdabao.pingmonitor.pingrobotapi.app.services.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import bbcdabao.pingmonitor.common.domain.coordination.Sysconfig;
import bbcdabao.pingmonitor.common.domain.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.domain.json.JsonConvert;
import bbcdabao.pingmonitor.common.domain.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.domain.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.pingrobotapi.app.services.IRegSysconfig;

public class RegSysconfigNotify extends BaseEventHandler implements IRegSysconfig, ApplicationRunner {
    private final Collection<WeakReference<INotify>> weakNotifyList = new ArrayList<>();
    private synchronized void onNotifySyn(Sysconfig sysconfig) {
        Iterator<WeakReference<INotify>> iterator = weakNotifyList.iterator();
        while (iterator.hasNext()) {
            INotify notify = iterator.next().get();
            if (notify != null) {
                try {
                    notify.onChange(sysconfig);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                iterator.remove();
            }
        }
    }
    private synchronized void regNotifySyn(INotify notify) {
        WeakReference<INotify> weakNotify = new WeakReference<>(notify);
        weakNotifyList.add(weakNotify);
    }
    public synchronized void regNotify(INotify notify) {
        regNotifySyn(notify);
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        start("/sysconfig");
    }
    @Override
    public void onEvent(CreatedEvent data) throws Exception {
        String strSysconfig = ByteDataConver
                .getInstance()
                .getConvertFromByteForString()
                .getValue(data.getData().getData());
        Sysconfig sysconfigNow = JsonConvert
                .getInstance()
                .fromJson(strSysconfig, Sysconfig.class);
        onNotifySyn(sysconfigNow);
    }
    @Override
    public void onEvent(ChangedEvent data) throws Exception {
        String strSysconfig = ByteDataConver
                .getInstance()
                .getConvertFromByteForString()
                .getValue(data.getData().getData());
        Sysconfig sysconfigNow = JsonConvert
                .getInstance()
                .fromJson(strSysconfig, Sysconfig.class);
        onNotifySyn(sysconfigNow);
    }
    @Override
    public void onEvent(DeletedEvent data) throws Exception {
    }
}
