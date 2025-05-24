package com.sky.service.impl.admin;

import com.aliyuncs.utils.StringUtils;
import com.sky.service.admin.ShopService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.sky.constant.RedisConstant.SHOP_STATUS_KEY;
import static com.sky.constant.StatusConstant.DISABLE;

@Service
public class ShopServiceImpl implements ShopService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Integer getStatus() {
        String s = stringRedisTemplate.opsForValue().get(SHOP_STATUS_KEY);
        if (StringUtils.isEmpty(s)) {
            return DISABLE;
        }
        return Integer.parseInt(s);
    }

    @Override
    public void setStatus(Integer status) {
        stringRedisTemplate.opsForValue().set(SHOP_STATUS_KEY, status.toString());
    }
}
