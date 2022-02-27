package com.nowcoder.community2.dao.elasticsearch;

import com.nowcoder.community2.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
//继承类中已经实现了增删改查等方法，我们直接调用即可
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
