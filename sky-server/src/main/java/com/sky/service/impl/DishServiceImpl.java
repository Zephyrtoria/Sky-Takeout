package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;

    @Resource
    private FlavorMapper flavorMapper;

    @Override
    // 出现对于多张数据表的操作，要开启事务操作来保证原子性
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        // 0. 将DTO转为实体类
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 1. 向菜品表中插入数据
        dishMapper.insert(dish);
        Long dishId = dish.getId();

        // 2. 向口味表中插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 判断口味集合是否存在
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            // 批量插入
            flavorMapper.insertBatch(flavors);
        }
    }
}
