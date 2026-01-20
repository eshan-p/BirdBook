package com.birdbook.repository;

import com.birdbook.models.Post;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostDAO extends MongoRepository<Post, ObjectId> {

    @Query("{ 'tags.?0': { $exists: true } }")
    List<Post> findByTagKey(List<String> tags);
}
