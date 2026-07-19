import subprocess

# 1. Get git version
result = subprocess.run(["git", "show", "HEAD:hotel-frontend/src/views/Login.vue"], capture_output=True)
data = result.stdout.decode("utf-8")

# 2. Fix hotel name
data = data.replace("\u6a59\u76ae\u8354\u996d\u5e97", "\u6a59\u76ae\u8354\u9152\u5e97")  # 橙皮荔饭店 -> 橙皮荔酒店

# 3. Add captcha import & request import
data = data.replace(
    'import { useUserStore } from "@/store/user";',
    'import { useUserStore } from "@/store/user";\nimport request from "@/utils/request";'
)

# 4. Add captcha refs
data = data.replace(
    "const showLoginDialog = ref(false);",
    "const showLoginDialog = ref(false);\nconst requireCaptcha = ref(false);\nconst captchaImage = ref(\x60\x60);\nconst captchaKey = ref(crypto.randomUUID());"
)

# 5. Add captcha fields to form
data = data.replace(
    "  empName: \"\",\n  password: \"\",\n});",
    "  empName: \"\",\n  password: \"\",\n  captcha: \"\",\n  captchaKey: \"\",\n});"
)

# 6. Add fetchCaptcha & checkFailCount before rules
data = data.replace(
    "const rules = {",
    "const fetchCaptcha = async () => {\n" +
    "  captchaKey.value = crypto.randomUUID();\n" +
    "  try {\n" +
    "    const res = await request.get(\x60/captcha?key=${captchaKey.value}\x60);\n" +
    "    if (res.code === 200) {\n" +
    "      captchaImage.value = res.data.image;\n" +
    "    }\n" +
    "  } catch (e) {\n" +
    "    console.error('captcha fetch error', e);\n" +
    "  }\n" +
    "};\n\n" +
    "const checkFailCount = async (username) => {\n" +
    "  try {\n" +
    "    const res = await request.get(\x60/login/failCount?username=${username}\x60);\n" +
    "    if (res.code === 200) {\n" +
    "      requireCaptcha.value = res.data.requireCaptcha;\n" +
    "      if (requireCaptcha.value) {\n" +
    "        form.captcha = '';\n" +
    "        form.captchaKey = captchaKey.value;\n" +
    "        await fetchCaptcha();\n" +
    "      }\n" +
    "    }\n" +
    "  } catch (e) {\n" +
    "    console.error(e);\n" +
    "  }\n" +
    "};\n\n" +
    "const rules = {"
)

# 7. Update handleLogin - add fail check before login
data = data.replace(
    "    const res = await login(form);\n" +
    "    if (res.code === 200) {",
    "    await checkFailCount(form.empName);\n" +
    "    if (requireCaptcha.value) {\n" +
    "      form.captchaKey = captchaKey.value;\n" +
    "    }\n" +
    "    const res = await login(form);\n" +
    "    if (res.code === 200) {"
)

# 8. On login failure, check failCount
data = data.replace(
    "} else {\n" +
    "      ElMessage.error(res.msg || '\u767b\u5f55\u5931\u8d25');\n" +
    "    }\n" +
    "  } catch (err) {\n" +
    "    console.error(err);\n" +
    "    ElMessage.error('\u767b\u5f55\u5f02\u5e38\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5');\n" +
    "  } finally {",
    "} else {\n" +
    "      ElMessage.error(res.msg || '\u767b\u5f55\u5931\u8d25');\n" +
    "      await checkFailCount(form.empName);\n" +
    "    }\n" +
    "  } catch (err) {\n" +
    "    console.error(err);\n" +
    "    await checkFailCount(form.empName);\n" +
    "  } finally {"
)

# 9. Add captcha template before login button
old_btn = (
    '        <el-form-item>\n' +
    '          <el-button\n' +
    '            type="primary"\n' +
    '            size="large"\n' +
    '            style="width: 100%"\n' +
    '            @click="handleLogin"\n' +
    '            :loading="loading"\n' +
    '          >\n' +
    '            \u767b \u5f55\n' +
    '          </el-button>\n' +
    '        </el-form-item>'
)

captcha_tpl = (
    '        <!-- \u9a8c\u8bc1\u7801 -->\n' +
    '        <el-form-item v-if="requireCaptcha" prop="captcha">\n' +
    '          <div style="display: flex; gap: 10px; align-items: stretch; width: 100%;">\n' +
    '            <el-input\n' +
    '              v-model="form.captcha"\n' +
    '              placeholder="\u9a8c\u8bc1\u7801"\n' +
    '              prefix-icon="Key"\n' +
    '              size="large"\n' +
    '              style="flex: 1;"\n' +
    '              @keyup.enter="handleLogin"\n' +
    '            />\n' +
    '            <img\n' +
    '              v-if="captchaImage"\n' +
    '              :src="captchaImage"\n' +
    '              alt="\u9a8c\u8bc1\u7801"\n' +
    '              style="height: 40px; cursor: pointer; border-radius: 4px; border: 1px solid #dcdfe6;"\n' +
    '              @click="fetchCaptcha"\n' +
    '              title="\u70b9\u51fb\u5237\u65b0\u9a8c\u8bc1\u7801"\n' +
    '            />\n' +
    '          </div>\n' +
    '        </el-form-item>\n' +
    '        <el-form-item>\n' +
    '          <el-button\n' +
    '            type="primary"\n' +
    '            size="large"\n' +
    '            style="width: 100%"\n' +
    '            @click="handleLogin"\n' +
    '            :loading="loading"\n' +
    '          >\n' +
    '            \u767b \u5f55\n' +
    '          </el-button>\n' +
    '        </el-form-item>'
)

data = data.replace(old_btn, captcha_tpl, 1)

# 10. Write result
with open("hotel-frontend/src/views/Login.vue", "w", encoding="utf-8") as f:
    f.write(data)

print("Done - Login.vue regenerated")

# Verify
with open("hotel-frontend/src/views/Login.vue", "rb") as f:
    raw = f.read()

# No BOM
print("BOM:", raw[:3] == b"\xef\xbb\xbf")

# Hotel name
idx = raw.find(b"brand-name")
if idx >= 0:
    s = raw.find(b">", idx) + 1
    e = raw.find(b"<", s)
    print("brand-name:", raw[s:e].decode("utf-8"))

# Captcha elements
for term in ["requireCaptcha", "captchaImage", "fetchCaptcha", "checkFailCount"]:
    print(f"{term}: {term in data}")
