<template>
  <div style="display: none"></div>
</template>

<script setup>
import request from "@/utils/request";

const print = async (orderId) => {
  // 1. 获取订单主信息
  const orderRes = await request.get(`/finance/order/bill/${orderId}`);
  const order = orderRes.data;
  // 2. 获取订单明细
  const detailRes = await request.get(`/finance/order/details/${orderId}`);
  const details = detailRes.data || [];

  // 按类型分组
  const foodItems = details.filter((d) => d.itemType === "餐饮");
  const ktvItems = details.filter((d) => d.itemType === "KTV");
  const otherItems = details.filter(
    (d) => d.itemType !== "餐饮" && d.itemType !== "KTV",
  );

  // 房间号带房型显示
  const roomDisplay =
    order.roomNumber + (order.roomType ? "-" + order.roomType : "");

  // 构建打印内容
  const printWindow = window.open("", "_blank");
  printWindow.document.write(`
    <html>
    <head>
      <title>结账单</title>
      <style>
        body { font-family: 'Microsoft YaHei', sans-serif; padding: 30px; }
        h2 { text-align: center; margin-bottom: 20px; }
        .info { margin-bottom: 20px; border-bottom: 1px solid #ddd; padding-bottom: 10px; }
        .info p { margin: 5px 0; }
        table { width: 100%; border-collapse: collapse; margin: 10px 0; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .total-row { font-weight: bold; background-color: #f9f9f9; }
        .footer { margin-top: 30px; text-align: center; font-size: 12px; color: #999; }
      </style>
    </head>
    <body>
      <h2>饭店管理系统 · 结账单</h2>
      <div class="info">
        <p><strong>订单号：</strong>${order.orderId}</p>
        <p><strong>客人姓名：</strong>${order.guestName}</p>
        <p><strong>房间号：</strong>${roomDisplay}</p>
        <p><strong>结账时间：</strong>${order.settleTime}</p>
        <p><strong>操作员：</strong>${order.operator || ""}</p>
      </div>

      <h3>消费明细</h3>
      ${
        foodItems.length
          ? `
        <h4>餐饮消费</h4>
        <table>
          <thead><tr><th>项目</th><th>数量</th><th>单价(¥)</th><th>金额(¥)</th></tr></thead>
          <tbody>
            ${foodItems.map((d) => `<tr><td>${d.itemName}</td><td>${d.quantity}</td><td>${d.price}</td><td>${d.amount}</td></tr>`).join("")}
          </tbody>
        </table>
      `
          : ""
      }
      ${
        ktvItems.length
          ? `
        <h4>KTV消费（不计入本次结算）</h4>
        <table>
          <thead><tr><th>项目</th><th>金额(¥)</th></tr></thead>
          <tbody>
            ${ktvItems.map((d) => `<tr><td>${d.itemName}</td><td>${d.amount}</td></tr>`).join("")}
          </tbody>
        </table>
      `
          : ""
      }
      ${
        otherItems.length
          ? `
        <h4>其他消费</h4>
        <table>
          <thead><tr><th>项目</th><th>数量</th><th>单价(¥)</th><th>金额(¥)</th></tr></thead>
          <tbody>
            ${otherItems.map((d) => `<tr><td>${d.itemName}</td><td>${d.quantity}</td><td>${d.price}</td><td>${d.amount}</td></tr>`).join("")}
          </tbody>
        </table>
      `
          : ""
      }

      <table class="total-table">
        <tr><td style="border:none; text-align:right;"><strong>消费总额：</strong></td><td style="border:none;">¥${order.totalAmount}</td></tr>
        <tr><td style="border:none; text-align:right;"><strong>押金总额：</strong></td><td style="border:none;">¥${order.depositTotal}</td></tr>
        <tr><td style="border:none; text-align:right;"><strong>退款金额：</strong></td><td style="border:none;">¥${order.refundAmount}</td></tr>
        <tr class="total-row"><td style="border:none; text-align:right;"><strong>实收/退：</strong></td><td style="border:none;"><span style="color:#f56c6c;">¥${(order.totalAmount - order.depositTotal).toFixed(2)}</span></td></tr>
      </table>

      <div class="footer">感谢惠顾，欢迎下次光临！</div>
    </body>
    </html>
  `);
  printWindow.document.close();
  printWindow.print();
  printWindow.close();
};

defineExpose({ print });
</script>
