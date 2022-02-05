package com.nowcoder.community2.dao;

import com.nowcoder.community2.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface DiscussPostMapper {
    /**
     * 分页显示帖子
     * @param userId 可有可无，为0就不根据用户id查询
     * @param offset 起始页码
     * @param limit 每页显示的帖子数量
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 查询帖子总数
     * @param userId 注意，由于userId是sql语句中if中的条件，并且该方法只有这一个参数，
     *               所以他必须加上注解@Param来给他起名，否则会报错，无法找到一个指定的参数
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 插入一个新的帖子
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 根据帖子id查询对应的帖子
     * 在点击帖子进入帖子详情页面时需要使用
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);
}
