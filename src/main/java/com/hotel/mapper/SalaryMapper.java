package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Salary;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface SalaryMapper extends BaseMapper<Salary> {

    @Select("SELECT * FROM salary WHERE emp_id = #{empId} AND `year_month` = #{yearMonth}")
    Salary selectByEmpIdAndMonth(Integer empId, String yearMonth);

    @Update("UPDATE salary SET bonus = #{bonus}, total_salary = base_salary - IFNULL(attendance_deduction,0) + #{bonus} WHERE salary_id = #{salaryId}")
    int updateBonusAndTotal(Integer salaryId, java.math.BigDecimal bonus);
}