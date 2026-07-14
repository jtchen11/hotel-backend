export const PAY_METHODS = {
  WECHAT: '微信',
  ALIPAY: '支付宝',
  CASH: '现金',
  CARD: '银行卡',
}

export const PAY_METHOD_OPTIONS = [
  { value: PAY_METHODS.WECHAT, label: '微信支付' },
  { value: PAY_METHODS.ALIPAY, label: '支付宝' },
  { value: PAY_METHODS.CASH, label: '现金' },
  { value: PAY_METHODS.CARD, label: '银行卡' },
]

// 默认为微信
export const DEFAULT_PAY_METHOD = PAY_METHODS.WECHAT
