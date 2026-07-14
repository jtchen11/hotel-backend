import request from "@/utils/request";

export const getRevenueByType = () => request.get("/api/gm/revenueByType");
export const getGuestSource = () => request.get("/api/gm/guestSource");
export const getOccupancyRate = (yearMonth) =>
  request.get("/api/gm/occupancyRate", { params: { yearMonth } });
export const getStaffRoomRatio = () => request.get("/api/gm/staffRoomRatio");
