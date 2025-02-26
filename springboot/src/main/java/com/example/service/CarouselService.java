package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Carousel;
import com.example.mapper.CarouselMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 轮播图信息表业务处理
 **/
@Service
public class CarouselService {

    @Resource
    private CarouselMapper carouselMapper;

    /**
     * 新增
     */
    public void add(Carousel carousel) {
        carousel.setTime(DateUtil.today());
        Account currentUser = TokenUtils.getCurrentUser();
        carousel.setUser(currentUser.getUsername());
        carouselMapper.insert(carousel);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        carouselMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            carouselMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Carousel carousel) {
        carouselMapper.updateById(carousel);
    }

    /**
     * 根据ID查询
     */
    public Carousel selectById(Integer id) {
        return carouselMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Carousel> selectAll(Carousel carousel) {
        return carouselMapper.selectAll(carousel);
    }

    /**
     * 分页查询
     */
    public PageInfo<Carousel> selectPage(Carousel carousel, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Carousel> list = carouselMapper.selectAll(carousel);
        return PageInfo.of(list);
    }

}