/**
 * Copyright 2025 bbcdabao Team
 */

import {
  defineComponent,
  h,
  ref
} from 'vue';
import {
  ElInput,
  ElOption,
  ElSelect,
  ElMessageBox  
} from 'element-plus';

/**
 * 手机号输入校验对话框，返回正确的手机号
 * @returns 
 */
export const promptMobilePhoneInput = async (
  t: (key: string) => string
): Promise<string> => {
  const { value } = await ElMessageBox.prompt(
    t('enterMobilePhone'),
    '',
    {
      confirmButtonText: t('confirm'),
      cancelButtonText:  t('cancelInfo'),
      inputPlaceholder:  t('enterMobilePhone'),
      inputPattern: /^1[3-9]\d{9}$/,
      inputErrorMessage: t('invalidPhone'),
      closeOnClickModal: false,
      distinguishCancelAndClose: true,
    }
  )
  return value;
}

/**
 * 数字输入对话框
 * @param t 国际化函数
 * @param title 对话框标题
 * @param min 最小值
 * @param max 最大值
 * @returns 输入数字
 */
export const promptNumberInput = async (
  t: (key: string) => string,
  title: string,
  min: number = Number.MIN_SAFE_INTEGER,
  max: number = Number.MAX_SAFE_INTEGER,
): Promise<number> => {
  const { value } = await ElMessageBox.prompt(
    title,
    '',
    {
      inputType: 'number',
      confirmButtonText: t('confirm'),
      cancelButtonText: t('cancelInfo'),
      inputPlaceholder: title,
      inputValidator: (val: string) => {
        const num = Number(val);
        if (isNaN(num)) return t('invalidNumber');
        if (num < min || num > max) return `${t('invalidNumber')}: ${min} ~ ${max}`;
        return true;
      },
      inputErrorMessage: `${t('invalidNumber')}: ${min} ~ ${max}`,
      closeOnClickModal: false,
      distinguishCancelAndClose: true,
    }
  );
  return Number(value);
};

/**
 * 报价对话框
 * @param t 
 * @param min 
 * @param max 
 * @returns 
 */
export const promptPriceInput = async (
  t: (key: string) => string,
  min = 0,
  max = Number.MAX_SAFE_INTEGER
): Promise<{ sale: boolean; value: number }> => {
  const sale = ref(true);
  const price = ref('');
  const errorMessage = ref('');

  const Content = defineComponent({
    setup() {
      return () =>
      h('div', {
          style: 'width: 265px;'
        }, [
        h(ElSelect, {
          modelValue: sale.value,
          'onUpdate:modelValue': (val: boolean) => (sale.value = val),
          placeholder: t('selectBuySell'),
          size: 'small',
          style: 'font-weight: bold;'
        },
        {
          default: () => [
            h(ElOption, { label: t('buy'), value: false,
              style: 'font-size: 12px; font-weight: bold;'}),
            h(ElOption, { label: t('sell'), value: true,
              style: 'font-size: 12px; font-weight: bold;'})
          ]
        }),
        h('div', {
          style: 'margin-top: 10px; font-size: 12px; font-weight: bold;'
        }, `${t('priceBt')}: ${min} ~ ${max}`),
        h(ElInput, {
          modelValue: price.value,
          'onUpdate:modelValue': (val: string) => (price.value = val),
          placeholder: t('inputPrice'),
          type: 'number',
          size: 'small',
          style: 'margin-top: 2px;'
        }),
        errorMessage.value ? h('div', { style: 'color: red; font-size: 12px;' }, errorMessage.value) : null
      ]);
    }
  });

  const result = await ElMessageBox({
    title: '',
    message: h(Content),
    showCancelButton: true,
    confirmButtonText: t('confirm'),
    cancelButtonText: t('cancelInfo'),
    closeOnClickModal: false,
    distinguishCancelAndClose: true,
    beforeClose: (action, _instance, done) => {
      if (action === 'confirm') {
        const num = parseFloat(price.value);
        if (isNaN(num)) {
          errorMessage.value = t('invalidNumber');
          return;
        }
        if (num < min || num > max) {
          errorMessage.value = `${t('invalidNumber')}: ${min} ~ ${max}`;
          return;
        }
      }
      done();
    }
  }).catch(() => {
    return null;
  });

  if (!result) throw new Error('User cancelled');

  return {
    sale: sale.value,
    value: parseFloat(Number(price.value).toFixed(2))
  };
};