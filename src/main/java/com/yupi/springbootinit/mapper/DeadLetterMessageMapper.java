package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.springbootinit.model.entity.DeadLetterMessage;

import java.util.Date;
import java.util.List;

public interface DeadLetterMessageMapper extends BaseMapper<DeadLetterMessage> {

    List<DeadLetterMessage> listByStatusAndTime(int i, Date threeDaysAgo);
}
