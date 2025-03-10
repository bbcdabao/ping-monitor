package bbcdabao.pingmonitor.pingrobotapi.app.services.impl;

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

import bbcdabao.pingmonitor.common.domain.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.domain.coordination.MasterKeeperTaskManager;
import bbcdabao.pingmonitor.common.domain.coordination.Sysconfig;
import bbcdabao.pingmonitor.common.infra.domainconfig.SpringContextHolder;
import bbcdabao.pingmonitor.pingrobotapi.app.services.IRegSysconfig;
import bbcdabao.pingmonitor.pingrobotapi.app.services.IRegSysconfig.INotify;
import bbcdabao.pingmonitor.pingrobotapi.infra.domainconfig.configs.RobotConfig;
import jakarta.annotation.PostConstruct;

public class MasterService implements ApplicationRunner, INotify {
    public class TaskMaster implements Job {
        @Override
        public void execute(JobExecutionContext context)
                throws JobExecutionException {
            try {
                CoordinationManager
                .getInstance()
                .taskFire(robotGroupName);
            } catch (Exception e) {
                throw new JobExecutionException(e.getMessage());
            }
        }
    }

    private AtomicReference<Sysconfig> sysconfigRef = new AtomicReference<>();
    private String robotGroupName = null;
    private JobKey jobKey = new JobKey("taskFire", "robot");
    private TriggerKey triggerKey = new TriggerKey("taskFire" + "Trigger", "robot");

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private IRegSysconfig regSysconfigNotify;

    private synchronized void updateJobCron() throws Exception {
        String cronExpression = sysconfigRef.get().getCron();
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
    private synchronized void createJobCron() throws Exception {
        String cronExpression = sysconfigRef.get().getCron();
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
    private synchronized void deleteJobCron() throws Exception {
        if (!scheduler.checkExists(jobKey)) {
            return;
        }
        scheduler.deleteJob(jobKey);
    }

    @PostConstruct
    public void init() {
        regSysconfigNotify.regNotify(this);
    }

    @Override
    public void onChange(Sysconfig config) throws Exception {
        sysconfigRef.set(config);
        updateJobCron();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        robotGroupName = SpringContextHolder.getBean(RobotConfig.class).getRobotGroupName();
        String patch = new StringBuilder()
                .append("/robot/register/")
                .append(robotGroupName)
                .append("/run-info/election")
                .toString();
        MasterKeeperTaskManager.getInstance().selectMasterNotify(patch,
                () -> {
                    createJobCron();
                },
                () -> {
                    deleteJobCron();
                }, null);
    }
}
