package com.hotel.controller.operation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.common.Result;
import com.hotel.common.UserContext;
import com.hotel.entity.Menu;
import com.hotel.entity.Stock;
import com.hotel.entity.StockIn;
import com.hotel.entity.StockOut;
import com.hotel.service.MenuService;
import com.hotel.service.StockInService;
import com.hotel.service.StockOutService;
import com.hotel.service.StockService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse")
@CrossOrigin
public class WarehouseController {

    @Resource
    private StockService stockService;

    @Resource
    private StockInService stockInService;

    @Resource
    private StockOutService stockOutService;

    // 库存列表（支持物品名称模糊查询）
    @GetMapping("/stock")
    public Result<List<Stock>> list(@RequestParam(required = false) String itemName) {
        List<Stock> list = stockService.getStockList(itemName);
        return Result.success(list);
    }

    // 入库
    @PostMapping("/stockIn")
    public Result<Void> stockIn(@RequestBody StockIn stockIn) {
        if (stockIn.getStockId() == null || stockIn.getQuantity() == null || stockIn.getQuantity() <= 0) {
            return Result.error(400, "入库参数错误");
        }
        // 自动计算总金额
        BigDecimal totalAmount = stockIn.getUnitPrice().multiply(BigDecimal.valueOf(stockIn.getQuantity()));
        stockIn.setTotalAmount(totalAmount);
        // 从上下文获取操作员
        stockIn.setOperator(UserContext.getEmpName());
        stockInService.saveIn(stockIn);
        return Result.success();
    }

    // 出库
    @PostMapping("/stockOut")
    public Result<Void> stockOut(@RequestBody StockOut stockOut) {
        if (stockOut.getStockId() == null || stockOut.getQuantity() == null || stockOut.getQuantity() <= 0) {
            return Result.error(400, "出库参数错误");
        }
        stockOut.setOperator(UserContext.getEmpName());
        // 如果前端没有传 purpose，可以设置默认值
        if (stockOut.getPurpose() == null) stockOut.setPurpose("");
        stockOutService.saveOut(stockOut);
        return Result.success();
    }

    // 库存预警列表（低于预警线的物品）
    @GetMapping("/warning")
    public Result<List<Stock>> warning() {
        List<Stock> warningList = stockService.getWarningStock();
        return Result.success(warningList);
    }
    // 获取所有菜品库存列表
    @GetMapping("/menuStocks")
    public Result<List<Menu>> listMenuStocks() {
        List<Menu> list = menuService.list();
        return Result.success(list);
    }

    // 菜品补货（增加库存）
    @PostMapping("/menuStock/add")
    public Result addMenuStock(@RequestParam Integer menuId, @RequestParam Integer addQuantity) {
        Menu menu = menuService.getById(menuId);
        if (menu == null) return Result.error("菜品不存在");
        menu.setStockQuantity(menu.getStockQuantity() + addQuantity);
        // 如果补货后库存>0，状态改为在售
        if (menu.getStockQuantity() > 0) {
            menu.setStatus("在售");
        }
        menuService.updateById(menu);
        return Result.success("补货成功");
    }
    @Resource
    private MenuService menuService;
    @PostMapping("/addStock")
    public Result<Map<String, Integer>> addStock(@RequestBody Stock stock) {
        if (stock.getItemName() == null || stock.getItemName().trim().isEmpty()) {
            return Result.error("物品名称不能为空");
        }
        // 校验名称唯一性
        QueryWrapper<Stock> qw = new QueryWrapper<>();
        qw.eq("item_name", stock.getItemName());
        if (stockService.count(qw) > 0) {
            return Result.error("该物品已存在，请直接选择入库");
        }
        // 设置默认值
        stock.setCurrentQuantity(0);
        stock.setUnitPrice(null);
        stock.setUpdateTime(LocalDateTime.now());
        // 如果前端没有传 warningLevel，设置默认预警值
        if (stock.getWarningLevel() == null) stock.setWarningLevel(10);
        stockService.save(stock);
        Map<String, Integer> result = new HashMap<>();
        result.put("stockId", stock.getStockId());
        return Result.success(result);
    }
    @GetMapping("/records")
    public Result<Map<String, Object>> getRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer stockId,
            @RequestParam(required = false) String type) {
        return Result.success(stockService.getStockRecords(page, size, stockId, type));
    }
}