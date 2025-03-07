package bbcdabao.pingmonitor.pingrobotapi.business.master.services;

import java.util.concurrent.atomic.AtomicReference;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import bbcdabao.pingmonitor.common.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.coordination.MasterKeeperTaskManager;
import bbcdabao.pingmonitor.common.coordination.Sysconfig;
import bbcdabao.pingmonitor.common.zkclientframe.BaseEventHandler;
import bbcdabao.pingmonitor.common.zkclientframe.event.ChangedEvent;
import bbcdabao.pingmonitor.common.zkclientframe.event.CreatedEvent;
import bbcdabao.pingmonitor.common.zkclientframe.event.DeletedEvent;
import bbcdabao.pingmonitor.pingrobotapi.config.RobotConfig;

public class TaskfireMasterService implements ApplicationRunner {
    public class TaskMaster implements Job {
        @Override
        public void execute(JobExecutionContext context)
                throws JobExecutionException {
            try {
                CoordinationManager
                .getInstance()
                .taskFire(RobotConfig.getInstance().getRobotGroupName());
            } catch (Exception e) {
                throw new JobExecutionException(e.getMessage());
            }
        }
    }
    public class MonitorSysConfig extends BaseEventHandler {
        public MonitorSysConfig() {
            this.start("/sysconfig");
        }
        @Override
        public void onEvent(CreatedEvent data) throws Exception {
            sysconfigRef.set(CoordinationManager.getInstance().getSysconfig());
        }
        @Override
        public void onEvent(ChangedEvent data) throws Exception {
            sysconfigRef.set(CoordinationManager.getInstance().getSysconfig());
            updateJobCron(sysconfigRef.get().getCron());
        }
        @Override
        public void onEvent(DeletedEvent data) throws Exception {
            sysconfigRef.set(null);
        }
    }
    private AtomicReference<Sysconfig> sysconfigRef = new AtomicReference<>();
    private MonitorSysConfig monitorSysConfig = null;
    private JobKey jobKey = new JobKey("taskFire", "robot");
    private TriggerKey triggerKey = new TriggerKey("taskFire" + "Trigger", "robot");
    @Autowired
    private Scheduler scheduler;
    public void updateJobCron(String cronExpression) throws Exception {
        if (!scheduler.checkExists(jobKey)) {
            return;
        }
        Trigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        scheduler.rescheduleJob(triggerKey, newTrigger);
    }
    public void createJobCron(String cronExpression) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(TaskMaster.class)
                .withIdentity(jobKey)
                .storeDurably()
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
    public void deleteJobCron() throws Exception {
        if (!scheduler.checkExists(jobKey)) {
            return;
        }
        scheduler.deleteJob(jobKey);
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String patch = new StringBuilder()
                .append("/robot/register/")
                .append(RobotConfig.getInstance().getRobotGroupName())
                .append("/run-info/election")
                .toString();
        MasterKeeperTaskManager.getInstance().selectMasterNotify(patch,
                () -> {
                    if (null != monitorSysConfig) {
                        monitorSysConfig = new MonitorSysConfig();
                    }
                    Sysconfig sysconfig = sysconfigRef.get();
                    if (null == sysconfig) {
                        throw new Exception("no get Sysconfig!");
                    }
                    try {
                        createJobCron(sysconfig.getCron());
                    } catch (Exception e) {
                    }
                },
                () -> {
                    monitorSysConfig = null;
                    try {
                        deleteJobCron();
                    } catch (Exception e) {
                    }
                }, null);
    }
}
