with open("hotel-frontend/src/views/Login.vue", "r", encoding="utf-8") as f:
    c = f.read()

# Insert captcha template before the login button form-item
old = """        <el-form-item>
          <el-button"""

new = """        <!-- 验证码 -->
        <el-form-item v-if="requireCaptcha" prop="captcha">
          <div style="display: flex; gap: 10px; align-items: stretch; width: 100%;">
            <el-input
              v-model="form.captcha"
              placeholder="验证码"
              prefix-icon="Key"
              size="large"
              style="flex: 1;"
              @keyup.enter="handleLogin"
            />
            <img
              v-if="captchaImage"
              :src="captchaImage"
              alt="验证码"
              style="height: 40px; cursor: pointer; border-radius: 4px; border: 1px solid #dcdfe6;"
              @click="fetchCaptcha"
              title="点击刷新验证码"
            />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button"""

c = c.replace(old, new, 1)

with open("hotel-frontend/src/views/Login.vue", "w", encoding="utf-8") as f:
    f.write(c)
print("Captcha template added")
