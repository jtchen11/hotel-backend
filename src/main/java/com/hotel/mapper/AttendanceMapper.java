package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Attendance;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceMapper extends BaseMapper<Attendance> {

    @Select("SELECT * FROM attendance WHERE emp_id = #{empId} AND DATE(work_date) = #{date}")
    Attendance selectByEmpIdAndDate(@Param("empId") Integer empId, @Param("date") LocalDate date);

    @Select("SELECT * FROM attendance WHERE emp_id = #{empId} AND DATE_FORMAT(work_date, '%Y-%m') = #{yearMonth}")
    List<Attendance> selectByEmpIdAndMonth(@Param("empId") Integer empId, @Param("yearMonth") String yearMonth);

    @Insert("INSERT INTO attendance (emp_id, work_date, status, check_in_time, check_out_time, work_hours, remark) " +
            "VALUES (#{empId}, #{workDate}, #{status}, #{checkInTime}, #{checkOutTime}, #{workHours}, #{remark}) " +
            "ON DUPLICATE KEY UPDATE status = VALUES(status), check_in_time = VALUES(check_in_time), " +
            "check_out_time = VALUES(check_out_time), work_hours = VALUES(work_hours), remark = VALUES(remark)")
    void insertOrUpdate(Attendance attendance);
}