/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package bbcdabao.pingmonitor.common.domain.coordination;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import bbcdabao.pingmonitor.common.domain.dataconver.ByteDataConver;
import bbcdabao.pingmonitor.common.domain.extraction.TemplateField;
import bbcdabao.pingmonitor.common.domain.json.JsonConvert;
import bbcdabao.pingmonitor.common.domain.zkclientframe.core.CuratorFrameworkInstance;

public class CoordinationManager {

    private static class Holder {
        private static final CoordinationManager INSTANCE = new CoordinationManager();
    }

    public static CoordinationManager getInstance() {
        return Holder.INSTANCE;
    }

    private CoordinationManager() {
    }

    public void deleteData(IPath path) throws Exception {
        try {
            CuratorFrameworkInstance
            .getInstance()
            .delete()
            .forPath(path.get());
        } catch (NoNodeException e) {
            e.printStackTrace();
        }
    }

    public void putData(IPath path, CreateMode mode, byte[] data) throws Exception {
        try {
            CuratorFrameworkInstance
            .getInstance()
            .create()
            .creatingParentsIfNeeded()
            .withMode(mode)
            .forPath(path.get(), data);
        } catch (NodeExistsException e) {
            CuratorFrameworkInstance
            .getInstance()
            .setData()
            .forPath(path.get(), data);
        }
    }

    public Stat getStat(IPath path) throws Exception {
        Stat stat = CuratorFrameworkInstance
                .getInstance()
                .checkExists()
                .forPath(path.get());
        return stat;
    }
    public byte[] getData(IPath path) throws Exception {
        byte[] data = CuratorFrameworkInstance
                .getInstance()
                .getData()
                .forPath(path.get());
        return data;
    }

    @FunctionalInterface
    public static interface IChildGetStat {
        void onStat(IPath childPath, String child, Stat stat);
    }
    @FunctionalInterface
    public static interface IChildGetData {
        void onData(IPath childPath, String child, byte[] data);
    }
    @FunctionalInterface
    public static interface IChildGetDataStat {
        void onData(IPath childPath, String child, byte[] data, Stat stat);
    }
    public List<String> getChildren(IPath path) throws Exception {
        List<String> children = CuratorFrameworkInstance
                .getInstance()
                .getChildren()
                .forPath(path.get());
        return children;
    }
    public List<String> getChildren(IPath path, IChildGetStat statFun) throws Exception {
        List<String> children = getChildren(path);
        if (null == statFun) {
            return children;
        }
        children.forEach(child -> {
            try {
                IPath childPath = IPath.getPath(path.get() + "/" + child);
                statFun.onStat(childPath, child,
                        getStat(childPath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return children;
    }
    public List<String> getChildren(IPath path, IChildGetData dataFun) throws Exception {
        List<String> children = getChildren(path);
        if (null == dataFun) {
            return children;
        }
        children.forEach(child -> {
            try {
                IPath childPath = IPath.getPath(path.get() + "/" + child);
                dataFun.onData(childPath, child,
                        getData(childPath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return children;
    }
    public List<String> getChildren(IPath path, IChildGetDataStat dataFun) throws Exception {
        List<String> children = getChildren(path);
        if (null == dataFun) {
            return children;
        }
        children.forEach(child -> {
            try {
                IPath childPath = IPath.getPath(path.get() + "/" + child);
                dataFun.onData(childPath, child,
                        getData(childPath),
                        getStat(childPath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return children;
    }

    public Sysconfig getSysconfig() throws Exception {
        return JsonConvert
                .getInstance()
                .fromJson(ByteDataConver
                        .getInstance()
                        .getConvertFromByteForString()
                        .getValue(getData(IPath.sysconfig())),
                        Sysconfig.class);
    }
    public void setSysconfig(Sysconfig sysconfig) throws Exception {
        putData(IPath.sysconfig(),
                CreateMode.PERSISTENT,
                ByteDataConver
                .getInstance()
                .getConvertToByteForString()
                .getData(JsonConvert
                        .getInstance()
                        .tobeJson(sysconfig)));
    }

    private static TypeReference<Map<String, TemplateField>> TYP_PLUGTEMPLATE = new TypeReference<Map<String, TemplateField>>() {};
    public Map<String, TemplateField> getPlugTemplate(String plugName) throws Exception {
        return JsonConvert
                .getInstance()
                .fromJson(ByteDataConver
                        .getInstance()
                        .getConvertFromByteForString()
                        .getValue(getData(IPath.plugTemplate(plugName))),
                        TYP_PLUGTEMPLATE);
    }
    public void setPlugTemplate(String plugName, Map<String, TemplateField> plugTemplate) throws Exception {
        putData(IPath.plugTemplate(plugName),
                CreateMode.PERSISTENT,
                ByteDataConver
                .getInstance()
                .getConvertToByteForString()
                .getData(JsonConvert
                        .getInstance()
                        .tobeJson(plugTemplate)));
    }

    public void regRobotInstance(String robotGroupName) throws Exception {
        String ipAddr = "none";
        try {
            InetAddress ip = InetAddress.getLocalHost();
            ipAddr = ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String instanceValue = new StringBuilder()
                .append(ipAddr)
                .append("@")
                .append(ProcessHandle.current().pid())
                .toString();
        putData(IPath.robotInstanceIdPath(robotGroupName),
                CreateMode.EPHEMERAL,
                ByteDataConver
                .getInstance()
                .getConvertToByteForString()
                .getData(instanceValue));
    }

    /**
     * Task config by taskName
     * @param taskName
     * @return
     * @throws Exception
     */
    public Properties getTaskConfigByTaskName(String taskName) throws Exception {
        return ByteDataConver
                .getInstance()
                .getConvertFromByteForProperties()
                .getValue(getData(IPath.taskConfigPath(taskName)));
    }
    public void setTaskConfigByTaskName(String taskName, Properties properties) throws Exception {
        putData(IPath.taskConfigPath(taskName),
                CreateMode.PERSISTENT,
                ByteDataConver
                .getInstance()
                .getConvertToByteForProperties()
                .getData(properties));
    }

    /**
     * Get plug name by taskName.
     * Just had get can not modify.
     * @param taskName
     * @return
     * @throws Exception
     */
    public String getPlugNameByTaskName(String taskName) throws Exception {
        return ByteDataConver
                .getInstance()
                .getConvertFromByteForString()
                .getValue(getData(IPath.taskPath(taskName)));
    }

    /**
     * Create one task
     * @param taskName
     * @param plugName
     * @param properties
     * @throws Exception
     */
    public void createTask(String taskName, String plugName, Properties properties) throws Exception {
        Map<String, TemplateField> plugTemplate =  getPlugTemplate(plugName);
        Set<String> plugFields = plugTemplate.keySet();
        for (String plugField : plugFields) {
            String plueFiledValue = properties.getProperty(plugField);
            if (ObjectUtils.isEmpty(plueFiledValue)) {
                throw new Exception(plugField + " is not config!");
            }
        }
        IPath path = IPath.taskPath(taskName); 
        putData(path,
                CreateMode.PERSISTENT,
                ByteDataConver
                .getInstance()
                .getConvertToByteForString()
                .getData(plugName));
        try {
            setTaskConfigByTaskName(taskName, properties);
        } catch (Exception e) {
            deleteData(path);
        }
    }
}
