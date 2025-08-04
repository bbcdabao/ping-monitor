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

import axios, {
  AxiosInstance,
  AxiosError,
  AxiosResponse,
  InternalAxiosRequestConfig
} from 'axios';
import {
  ElMessage
} from 'element-plus';

import emitter from '@/event-bus';
import i18n from '@/i18n';

const service: AxiosInstance = axios.create({
  timeout: 5000
});

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    return config;
  },
  (error: AxiosError) => {
    console.log(error);
    return Promise.reject(error);
  }
);

const errorHandlerMap = new Map<number, () => void>([
  [401, () => {
    ElMessage.error('401 error');
    emitter.emit('go-login');
  }],
  [403, () => {
    ElMessage.error('403 error');
    emitter.emit('go-login');
  }],
  /*
  [500, () => {
    const failInfo = i18n.global.t('failInfo');
    ElMessage.error(failInfo + '500');
  }],
  */
]);

/**
 * 统一错误处理分为两种错误
 * 1. 返回2xx 状态，但是业务处理异常 显示黄色警告
 * 2. 返回错误 显示红色
 */
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const rdata = response.data;
    if (!rdata.success) {
      const failBusiness = i18n.global.t('failBusiness');
      const failBusinessTest = failBusiness + (rdata.message || 'Unknown business error!');
      ElMessage.warning(failBusinessTest);
      return Promise.reject(new Error(failBusinessTest));
    }
    return rdata.data;
  },
  (error: AxiosError) => {
    const status = error.response?.status;
    const handler = status ? errorHandlerMap.get(status) : null;
    if (handler) {
      handler();
    } else {
      const failInfo = i18n.global.t('failInfo');
      const failInfoText = failInfo + (error.response.data || 'Unknown server error!');
      ElMessage.error(failInfoText);
    }
    console.log(error);
    return Promise.reject(error);
  }
);

export default service;