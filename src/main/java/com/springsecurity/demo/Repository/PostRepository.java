package com.springsecurity.demo.Repository;

import com.springsecurity.demo.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
