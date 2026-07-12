import request from "@/utils/request";

export const getRevenueByType = () => request.get("/gm/revenueByType");
export const getGuestSource = () => request.get("/gm/guestSource");
export const getOccupancyRate = (yearMonth) =>
  request.get("/gm/occupancyRate", { params: { yearMonth } });
export const getStaffRoomRatio = () => request.get("/gm/staffRoomRatio");
