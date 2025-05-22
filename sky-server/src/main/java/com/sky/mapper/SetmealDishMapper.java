package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据id批量查询
     *
     * @param ids
     * @return
     */
    List<Long> getSetmealIdByDishIds(List<Long> ids);


    /**
     * 修改套餐信息
     *
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);
}
