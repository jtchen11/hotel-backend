<template>
  <div style="display: none;"></div>
</template>

<script setup>
import request from '@/utils/request'

const print = async (salaryId) => {
  try {
    // 1. 获取工资详情（包含员工信息和各项明细）
    const res = await request.get(`/hr/salary/detail/${salaryId}`)
    const data = res.data

    // 计算扣款明细
    const deductions = []
    if (data.lateCount > 0) {
      deductions.push({ name: '迟到扣款', amount: data.lateCount * 20, count: data.lateCount, unit: '次', rate: '20元/次' })
    }
    if (data.earlyLeaveCount > 0) {
      deductions.push({ name: '早退扣款', amount: data.earlyLeaveCount * 20, count: data.earlyLeaveCount, unit: '次', rate: '20元/次' })
    }
    if (data.absentCount > 0) {
      const dailyWage = (data.baseSalary / 21.75).toFixed(2)
      deductions.push({
        name: '明确旷工扣款',
        amount: data.absentCount * dailyWage,
        count: data.absentCount,
        unit: '天',
        rate: `日工资 ¥${dailyWage}`
      })
    }
    if (data.missingDays > 0) {
      const dailyWage = (data.baseSalary / 21.75).toFixed(2)
      deductions.push({
        name: '缺勤/无记录扣款',
        amount: data.missingDays * dailyWage,
        count: data.missingDays,
        unit: '天',
        rate: `日工资 ¥${dailyWage}（无打卡记录）`
      })
    }
    if (data.leaveDays > 0) {
      const dailyWage = (data.baseSalary / 21.75).toFixed(2)
      deductions.push({ name: '请假扣款', amount: data.leaveDays * dailyWage, count: data.leaveDays, unit: '天', rate: `日工资 ¥${dailyWage}` })
    }

    // 构建打印内容
    const printWindow = window.open('', '_blank')
    printWindow.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="UTF-8">
        <title>工资条 - ${data.empName} ${data.yearMonth}</title>
        <style>
          * { margin: 0; padding: 0; box-sizing: border-box; }
          body {
            font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
            padding: 40px 50px;
            background: #fff;
            color: #333;
          }
          .slip-container {
            max-width: 700px;
            margin: 0 auto;
            border: 2px solid #1f68b9;
            border-radius: 12px;
            padding: 30px 35px;
            background: #fafcff;
          }
          .header {
            text-align: center;
            border-bottom: 2px solid #1f68b9;
            padding-bottom: 18px;
            margin-bottom: 22px;
          }
          .header h2 {
            font-size: 24px;
            color: #1f68b9;
            letter-spacing: 4px;
            margin-bottom: 4px;
          }
          .header .sub {
            font-size: 13px;
            color: #909399;
          }
          .emp-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 6px 20px;
            background: #f5f8fc;
            padding: 14px 18px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
          }
          .emp-info .label { color: #909399; }
          .emp-info .value { font-weight: 500; color: #303133; }
          .section-title {
            font-size: 16px;
            font-weight: 600;
            color: #1f68b9;
            margin: 18px 0 10px 0;
            padding-bottom: 6px;
            border-bottom: 1px solid #e4e7ed;
          }
          .salary-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 6px 30px;
            padding: 8px 4px;
            font-size: 14px;
          }
          .salary-grid .item {
            display: flex;
            justify-content: space-between;
            padding: 6px 0;
            border-bottom: 1px dashed #f0f2f5;
          }
          .salary-grid .item .label { color: #606266; }
          .salary-grid .item .amount { font-weight: 500; }
          .salary-grid .item .amount.positive { color: #67c23a; }
          .salary-grid .item .amount.negative { color: #f56c6c; }
          .deduction-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
            margin: 8px 0;
          }
          .deduction-table th {
            background: #f0f7ff;
            color: #1f68b9;
            padding: 8px 12px;
            text-align: left;
            border: 1px solid #e4e7ed;
          }
          .deduction-table td {
            padding: 7px 12px;
            border: 1px solid #e4e7ed;
          }
          .deduction-table .total-row {
            background: #f5f8fc;
            font-weight: 600;
          }
          .total-line {
            display: flex;
            justify-content: space-between;
            padding: 14px 4px;
            margin-top: 16px;
            border-top: 2px solid #1f68b9;
            font-size: 18px;
          }
          .total-line .label { font-weight: 600; color: #303133; }
          .total-line .amount { font-weight: 700; color: #f56c6c; }
          .footer {
            text-align: center;
            margin-top: 22px;
            padding-top: 14px;
            border-top: 1px solid #e4e7ed;
            font-size: 12px;
            color: #c0c4cc;
          }
          .footer .stamp {
            color: #1f68b9;
            font-weight: 600;
            letter-spacing: 2px;
          }
          @media print {
            body { padding: 20px; }
            .slip-container { border: 1px solid #ccc; }
          }
        </style>
      </head>
      <body>
        <div class="slip-container">
          <div class="header">
            <h2>饭 店 管 理 系 统</h2>
            <div class="sub">工资条 · ${data.yearMonth}</div>
          </div>

          <div class="emp-info">
            <div><span class="label">员工姓名</span>：<span class="value">${data.empName}</span></div>
            <div><span class="label">部门</span>：<span class="value">${data.department || '-'}</span></div>
            <div><span class="label">职务</span>：<span class="value">${data.position || '-'}</span></div>
            <div><span class="label">发放状态</span>：<span class="value" style="color:${data.payStatus === '已发放' ? '#67c23a' : '#f56c6c'}">${data.payStatus}</span></div>
          </div>

          <div class="section-title">📊 薪酬明细</div>
          <div class="salary-grid">
            <div class="item"><span class="label">基本工资</span><span class="amount">¥${data.baseSalary.toFixed(2)}</span></div>
            <div class="item"><span class="label">绩效奖金</span><span class="amount positive">¥${(data.bonus || 0).toFixed(2)}</span></div>
            <div class="item"><span class="label">加班费</span><span class="amount positive">¥${(data.overtimePay || 0).toFixed(2)}</span></div>
            <div class="item"><span class="label">其他加项</span><span class="amount positive">¥${(data.otherAdd || 0).toFixed(2)}</span></div>
            <div class="item"><span class="label">其他扣款</span><span class="amount negative">-¥${(data.otherDeduct || 0).toFixed(2)}</span></div>
          </div>

          ${deductions.length ? `
            <div class="section-title">📋 考勤扣款明细</div>
            <table class="deduction-table">
              <thead>
                <tr><th>扣款项目</th><th>次数/天数</th><th>扣款标准</th><th style="text-align:right;">扣款金额</th></tr>
              </thead>
              <tbody>
                ${deductions.map(d => `
                  <tr>
                    <td>${d.name}</td>
                    <td>${d.count} ${d.unit}</td>
                    <td>${d.rate}</td>
                    <td style="text-align:right; color:#f56c6c;">-¥${d.amount.toFixed(2)}</td>
                  </tr>
                `).join('')}
                <tr class="total-row">
                  <td colspan="3" style="text-align:right;">扣款合计</td>
                  <td style="text-align:right; color:#f56c6c;">-¥${data.attendanceDeduction.toFixed(2)}</td>
                </tr>
              </tbody>
            </table>
          ` : '<p style="color:#909399; font-size:13px; padding:6px 0;">本月无考勤扣款</p>'}

          <div class="total-line">
            <span class="label">实发工资</span>
            <span class="amount">¥${data.totalSalary.toFixed(2)}</span>
          </div>

          <div class="footer">
            <span class="stamp">· 财务专用 ·</span><br>
            本工资条仅供核对使用，如有异议请于三日内与财务部联系。
          </div>
        </div>
      </body>
      </html>
    `)
    printWindow.document.close()
    printWindow.print()
    // 打印完成后关闭窗口（延迟以允许打印）
    setTimeout(() => printWindow.close(), 1000)
  } catch (err) {
    console.error('打印工资条失败', err)
    alert('打印失败，请检查网络或联系管理员')
  }
}

defineExpose({ print })
</script>