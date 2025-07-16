<!-- Copyright 2025 bbcdabao Team -->

<template>
  <div class="login-bg">
    <div class="login-bg-image" />
    <div class="login-title" />
    <div class="login-container">
      <div class="login-header">
        <lucide-languages />
        <vLanguage style="margin-left: 6px; width: 80px;" />
      </div>
      <el-form
        style="width: 100%;"
        :model="usernameLogin"
        :rules="usernameLoginRules"
        ref="usernameLoginRef"
        size="large"
      >
        <el-form-item
          style="margin-bottom: 20px; width: 100%;"
          prop="username"
        >
          <el-input
            v-model="usernameLogin.username"
            :placeholder="$t('userName')"
          >
            <template #prepend>
              <i-material-symbols-person-outline-rounded />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item
          style="margin-bottom: 20px; width: 100%;"
          prop="password"
        >
          <el-input
            v-model="usernameLogin.password"
            :placeholder="$t('password')"
            type="password"
            show-password
            @keyup.enter="loginInfoSubmit()"
          >
            <template #prepend>
              <i-material-symbols-lock-sharp />
            </template>
          </el-input>
        </el-form-item>
        <div>
          <el-checkbox class="pwd-checkbox" v-model="usernameLoginChecked" :label="$t('rememberPassword')" />
        </div>
        <div style="width: 100%;" >
          <el-button
            class="login-btn"
            type="primary"
            size="large"
            @click="loginInfoSubmit()"
          >
            {{ $t('login') }}
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>
<script setup lang="ts">

import {
  ref,
  toRaw,
  reactive
} from 'vue';
import {
  useI18n
} from 'vue-i18n';
import {
  useRouter
} from 'vue-router';
import {
  ElMessage
} from 'element-plus';
import type {
  FormInstance,
  FormRules
} from 'element-plus';
import {
  postUsernamelogin
} from '@/api';
import {
  useHeaderStore
} from '@/store/header';
import {
  runWithErrorMessage
} from '@/utils';
import type {
  UsernameLoginPayload,
  LoginResponse
} from '@/types/login-sub';
import vLanguage from '@/components/language.vue';

const { t } = useI18n();
const router = useRouter();
const header = useHeaderStore();

/**
 * UsernameLogin
 */
const usernameLoginStr = localStorage.getItem('username-login');
const usernameLoginChecked = ref(usernameLoginStr ? true : false);

const usernameLogin = reactive<UsernameLoginPayload> (
  usernameLoginStr ? JSON.parse(usernameLoginStr) : {
    username: '',
    password: ''
  }
);
const usernameLoginRules: FormRules = {
  username: [
    {
      required: true,
      message: t('enterUserName'),
      trigger: 'blur',
    },
  ],
  password: [
    {
      required: true,
      message: t('enterUserPassword'),
      trigger: 'blur'
    }
  ],
};

const usernameLoginRef = ref<FormInstance | null>(null);
const loginInfoSubmit = () => {
  runWithErrorMessage( async () => {
    const valid = await usernameLoginRef.value.validate();
    if (!valid) throw new Error(t('formValidationFailed'));
    const response = await postUsernamelogin(toRaw(usernameLogin));
    header.setTitlesp(response.id);
    if (usernameLoginChecked.value) {
      localStorage.setItem('username-login', JSON.stringify(toRaw(usernameLogin)));
    } else {
      localStorage.removeItem('username-login');
    }
    router.replace('/');
    ElMessage.success(t('loginSuccess'));
  });
};

</script>
<style scoped>
.login-bg {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-height: 100vh;
  overflow: hidden;
}
.login-bg-image {
  position: fixed;
  top: 0;
  left: 0;
  z-index: -1;
  width: 100vw;
  height: 100vh;
  background: url(/img/login-bg-view.png);
  background-size: cover;
}
.login-title {
  height: 90px;
  width: 600px;
  margin-bottom: 20px;
  background: url(/img/login-bg-view-title.png);
  background-size: cover;
}
.login-header {
  display: flex;
  align-items: center;
  justify-content: left;
  margin-bottom: 20px;
}
.login-container {
  width: 360px;
  border-radius: 4px;
  color: var(--header-color);
  background: var(--header-bg-color);
  padding: 20px 50px 20px;
  box-sizing: border-box;
}
.login-btn {
  display: block;
  width: 100%;
}
</style>