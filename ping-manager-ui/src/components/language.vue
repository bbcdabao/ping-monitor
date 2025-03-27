<!-- Language selection -->
<template>
    <div>
        <el-dropdown trigger="click" @command="handleLangCommand">
            <span class="el-dropdown-link">
                {{ showLangs[idxLanguage] }}
                <el-icon class="el-icon--right">
                    <arrow-down />
                </el-icon>
            </span>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item v-for="(item, index) in showLangs" divided :command="index" :key="index">
                        {{ item }}
                    </el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>      
    </div>
</template>
<script setup lang="ts">
import { defineComponent, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const { t, locale } = useI18n();

const showLangs: Record<string, string> = {};
showLangs['en'] = 'EN';
showLangs['zh'] = '中文';

const getLanguage = () => {
    locale.value = localStorage.getItem('ping-monitor-lang') as 'en' | 'zh' || 'en';
    return locale.value;
}
const idxLanguage = ref(getLanguage());
const handleLangCommand = (command: string) => {
    localStorage.setItem('ping-monitor-lang', command);
    idxLanguage.value = getLanguage();
}

watch(idxLanguage, (newVal, oldVal) => {
    window.location.reload();
});

</script>
<style scoped>
.el-dropdown-link {
    color: var(--header-text-color);
    cursor: pointer;
    display: flex;
    align-items: center;
}
.el-dropdown-menu__item {
    text-align: center;
}
</style>
