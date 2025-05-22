package com.sky.mapper;

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
}
