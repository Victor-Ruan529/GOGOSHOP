package com.example.service;

import com.example.entity.Carousel;
import com.example.mapper.CarouselMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * 轮播图业务处理
 */
@Service
public class CarouselService {

    @Resource
    private CarouselMapper carouselMapper;

    /** 发布时间和发布人由服务端填写 */
    public void add(Carousel carousel) {
        carousel.setId(null);
        carousel.setTime(LocalDate.now());
        carousel.setUser(TokenUtils.requireCurrentUser().getUsername());
        carouselMapper.insert(carousel);
    }

    public void deleteById(Integer id) {
        carouselMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        ids.forEach(carouselMapper::deleteById);
    }

    public void updateById(Carousel carousel) {
        carousel.setTime(null);
        carousel.setUser(null);
        carouselMapper.updateById(carousel);
    }

    public Carousel selectById(Integer id) {
        return carouselMapper.selectById(id);
    }

    public List<Carousel> selectAll(Carousel carousel) {
        return carouselMapper.selectAll(carousel);
    }

    public PageInfo<Carousel> selectPage(Carousel carousel, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(carouselMapper.selectAll(carousel));
    }
}
