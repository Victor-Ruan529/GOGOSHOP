package com.example.service;

import com.example.entity.Notice;
import com.example.mapper.NoticeMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * 公告业务处理
 */
@Service
public class NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    /** 发布时间和发布人由服务端填写 */
    public void add(Notice notice) {
        notice.setId(null);
        notice.setTime(LocalDate.now());
        notice.setUser(TokenUtils.requireCurrentUser().getUsername());
        noticeMapper.insert(notice);
    }

    public void deleteById(Integer id) {
        noticeMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        ids.forEach(noticeMapper::deleteById);
    }

    public void updateById(Notice notice) {
        notice.setTime(null);
        notice.setUser(null);
        noticeMapper.updateById(notice);
    }

    public Notice selectById(Integer id) {
        return noticeMapper.selectById(id);
    }

    public List<Notice> selectAll(Notice notice) {
        return noticeMapper.selectAll(notice);
    }

    public PageInfo<Notice> selectPage(Notice notice, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(noticeMapper.selectAll(notice));
    }
}
