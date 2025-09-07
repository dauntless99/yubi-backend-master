package com.yupi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@TableName(value ="deadlettermessage")
@Data
public class DeadLetterMessage implements Serializable{


        @TableId(type = IdType.ASSIGN_ID)
        private Integer id;

        /**
         * 文本
         */
        private String messageContent;


        /**
         * 创建时间
         */
        private Date createTime;

        /**
        * 状态
        */
        private Integer status;


        /**
        * 创建时间
        */
        private Date updateTime;
        /**
         * 是否删除
         */
        @TableLogic
        private Integer isDelete;

        @TableField(exist = false)
        private static final long serialVersionUID = 1L;
    }

