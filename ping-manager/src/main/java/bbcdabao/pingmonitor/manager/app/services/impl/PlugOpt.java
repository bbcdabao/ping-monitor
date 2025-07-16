/**
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

package bbcdabao.pingmonitor.manager.app.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import bbcdabao.pingmonitor.common.infra.coordination.CoordinationManager;
import bbcdabao.pingmonitor.common.infra.coordination.IPath;
import bbcdabao.pingmonitor.manager.app.module.PlugInfo;
import bbcdabao.pingmonitor.manager.app.services.IPlugOpt;

@Service
public class PlugOpt implements IPlugOpt {
    private PlugInfo getPlugInfo(String plugName) throws Exception {
        CoordinationManager cm = CoordinationManager.getInstance();
        PlugInfo plugInfo = new PlugInfo();
        plugInfo.setPlugName(plugName);
        plugInfo.setPlugTemp(cm.getPlugTemplate(plugName));
        return plugInfo;
    }
    @Override
    public Collection<PlugInfo> getPlugInfos(String plugName) throws Exception {
        Collection<PlugInfo> plugInfos = new ArrayList<>();
        if (ObjectUtils.isEmpty(plugName)) {
            plugInfos.add(getPlugInfo(plugName));
            return plugInfos;
        }
        CoordinationManager cm = CoordinationManager.getInstance();
        List<String> plugNames = cm.getChildren(IPath.plugTemplate());
        for (String plugNameGet : plugNames) {
            plugInfos.add(getPlugInfo(plugNameGet));
        }
        return plugInfos;
    }
    @Override
    public void deletePlug(String plugName) throws Exception {
        CoordinationManager cm = CoordinationManager.getInstance();
        cm.deleteData(IPath.plugTemplate(plugName));
    }
}