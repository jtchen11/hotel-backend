import request from "@/utils/request";

// ========== 餐饮模块 ==========
export const listMenu = (category) => {
  return request.get("/dining/menu", { params: { category } });
};
export const orderDish = (data) => {
  return request.post("/dining/order", data);
};
export const getUnsettled = (guestId) => {
  return request.get("/dining/unsettled", { params: { guestId } });
};

// ========== KTV 模块 ==========
export const listKtvRooms = () => {
  return request.get("/ktv/rooms");
};
export const startKtv = (ktvId, guestId) => {
  return request.post("/ktv/start", { ktvId, guestId });
};
export const endKtv = (guestId) => {
  return request.post("/ktv/end", { guestId });
};
export const prepareEndKtv = (guestId) => {
  return request.get("/ktv/prepare", { params: { guestId } });
};
export const changeKtvRoomStatus = (ktvId, status) => {
  return request.put(`/ktv/room/${ktvId}/status`, null, { params: { status } });
};

// ========== 库房模块【修改入库、出库url】 ==========
export const listStock = (params) =>
  request.get("/warehouse/stock", { params });
export const stockIn = (data) => request.post("/warehouse/in", data);
export const stockOut = (data) => request.post("/warehouse/out", data);
export const getWarningList = () => request.get("/warehouse/warning");
