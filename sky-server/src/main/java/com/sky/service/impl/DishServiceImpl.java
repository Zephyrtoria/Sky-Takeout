package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.sky.constant.MessageConstant.DISH_BE_RELATED_BY_SETMEAL;
import static com.sky.constant.MessageConstant.DISH_ON_SALE;
import static com.sky.constant.StatusConstant.ENABLE;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;

    @Resource
    private FlavorMapper flavorMapper;

    @Resource
    private SetmealDishMapper setmealDishMapper;


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

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        long total = page.getTotal();
        List<DishVO> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        // 1. 判断当前菜品能否删除
        // 1.1 是否在起售中?
        List<Dish> dishes = dishMapper.getBatchByIds(ids);
        for (Dish dish : dishes) {
            if (dish.getStatus().equals(ENABLE)) {
                throw new DeletionNotAllowedException(DISH_ON_SALE);
            }
        }

        // 1.2 是否被套餐关联?
        List<Long> setmealIdByDishIds = setmealDishMapper.getSetmealIdByDishIds(ids);
        if (setmealIdByDishIds != null && !setmealIdByDishIds.isEmpty()) {
            throw new DeletionNotAllowedException(DISH_BE_RELATED_BY_SETMEAL);
        }

        // 2. 可以删除，删除菜品表中的数据
        dishMapper.deleteBatchByIds(ids);

        // 3. 删除口味表中的口味数据
        flavorMapper.deleteBatchByDishIds(ids);
    }
}
