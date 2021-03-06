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
     *
     * @param userId 可有可无，为0就不根据用户id查询
     * @param offset 起始页码
     * @param limit  每页显示的帖子数量
     * @param orderMode 排序规则（默认为0表示事件排序，为1表示按照热度排序
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    /**
     * 查询帖子总数
     *
     * @param userId 注意，由于userId是sql语句中if中的条件，并且该方法只有这一个参数，
     *               所以他必须加上注解@Param来给他起名，否则会报错，无法找到一个指定的参数
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 插入一个新的帖子
     *
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 根据帖子id查询对应的帖子
     * 在点击帖子进入帖子详情页面时需要使用
     *
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 修改帖子的回复数量
     *
     * @param id
     * @param commentCount
     * @return
     */
    int updateCommentCount(int id, int commentCount);

    //    修改帖子类型
    int updateType(int id, int type);

    //    修改帖子状态
    int updateStatus(int id, int status);

    //    修改帖子权重
    int updateScore(int id, double score);
}
