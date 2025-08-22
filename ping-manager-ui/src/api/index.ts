/**
 * Licensed to the bbcdabao Team under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. The bbcdabao Team licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import type {
  UsernameLoginPayload,
  LoginResponse
} from '@/types/login-sub';
import type {
  PlugInfo
} from '@/types/plug-sub';
import type {
  AddTaskRobotGroupsPayload,
  AddTaskPayload,
  TaskInfo
} from '@/types/task-sub';
import type {
  ResultDetailInfo
} from '@/types/result-sub';
import type {
  CheckRobotGroupInfo,
  RobotGroupInfo
} from '@/types/robot-sub';
import type {
  SysconfigPayload,
  SysconfigInfo
} from '@/types/sysconfig-sub';

import request from '@/utils/request';

/*
url:
  The requested server URL. Just need to be specified in a separate request.

method:
  Request method, the default is GET.
  
baseURL:
  Concatenate baseURL and url into a complete URL. If url is an absolute URL,
  it will not be concatenated.

headers:
  Custom HTTP headers.

params:
  URL parameters will be spliced ​​to the end of the URL.

data:
  Seeking volume data is only applicable to PUT, POST, PATCH and other methods.

timeout:
  Specifies the request timeout in milliseconds.

withCredentials:
  Whether credentials are required for cross-domain requests.

responseType:
  The expected data type of the server response. Commonly used ones include json,
  text, blob, and arraybuffer.

auth:
  HTTP Basic Authentication { username: '...', password: '...' }.
  
proxy: 
*/

/**
 * 用户登录
 * @param body
 * @returns 
 */
export const postUsernamelogin = (body: UsernameLoginPayload) : Promise<LoginResponse> => {
  return request({
    url: '/openapi/usernamelogin',
    method: 'post',
    data: body
  });
};

/**
 * 退出登录
 * @returns 
 */
export const postLogout = () : Promise<any> => {
  return request({
    url: '/openapi/logout',
    method: 'post'
  });
};

/**
 * 获取插件
 * @param plugName 
 * @returns 
 */
export const getPlugInfos = (plugName: string | null) : Promise<PlugInfo[]> => {
  return request({
    url: plugName ? `/api/plugs/${plugName}` : '/api/plugs',
    method: 'get'
  });
};

/**
 * 添加任务
 * @param taskName 
 * @param body 
 * @returns 
 */
export const postAddTask = (taskName: string, body: AddTaskPayload) : Promise<any> => {
  return request({
    url: `/api/tasks/${taskName}`,
    method: 'post',
    data: body
  });
};

/**
 * 删除任务
 * @param taskName 
 * @returns 
 */
export const deleteAddTask = (taskName: string) : Promise<any> => {
  return request({
    url: `/api/tasks/${taskName}`,
    method: 'delete'
  });
};

/**
 * 获取任务
 * @param taskName 
 * @returns 
 */
export const getTaskInfos = (taskName: string | null) : Promise<TaskInfo[]> => {
  return request({
    url: taskName ? `/api/tasks/${taskName}` : '/api/tasks',
    method: 'get',
  });
};

/**
 * 获取任务对应的机器人组
 * @param taskName 
 * @returns 
 */
export const getRobotGroupsByTaskName = (taskName: string) : Promise<RobotGroupInfo[]> => {
  return request({
    url: `/api/tasks/${taskName}/robot-groups`,
    method: 'get',
  });
};

/**
 * 获取任务对应的机器人组
 * @param taskName 
 * @returns 
 */
export const getCheckRobotGroupInfoByTaskName = (taskName: string) : Promise<CheckRobotGroupInfo[]> => {
  return request({
    url: `/api/tasks/${taskName}/check-robot-groups`,
    method: 'get',
  });
};


/**
 * 添加任务对应的机器人组
 * @param taskName 
 * @param body 
 * @returns 
 */
export const postRobotGroupsByTaskName = (taskName: string, body: AddTaskRobotGroupsPayload) : Promise<any> => {
  return request({
    url: `/api/tasks/${taskName}/robot-groups`,
    method: 'post',
    data: body
  });
};

/**
 * 获取机器人组信息
 * @param robotGroupName 
 * @returns 
 */
export const getRobotGroups = (robotGroupName: string | null) : Promise<RobotGroupInfo[]> => {
  return request({
    url: robotGroupName ? `/api/robot/groups/${robotGroupName}` : '/api/robot/groups',
    method: 'get',
  });
};

/**
 * 获取结果
 * @param taskName 
 * @returns 
 */
export const getResultDetailInfos = (taskName: string | null) : Promise<ResultDetailInfo[]> => {
  return request({
    url: taskName ? `/api/result/details/${taskName}` : '/api/result/details',
    method: 'get',
  });
};

/**
 * 删除结果
 * @param taskName 
 * @returns 
 */
export const deleteResultDetailInfos = (taskName: string | null) : Promise<ResultDetailInfo[]> => {
  return request({
    url: taskName ? `/api/result/details/${taskName}` : '/api/result/details',
    method: 'delete',
  });
};

/**
 * 获取配置信息
 * @returns 
 */
export const getSysconfigInfo = () : Promise<SysconfigInfo> => {
  return request({
    url: '/api/sysconfig',
    method: 'get',
  });
};

export const postSysconfigInfo = (body: SysconfigPayload) : Promise<any> => {
  return request({
    url: '/api/sysconfig',
    method: 'post',
    data: body
  });
};