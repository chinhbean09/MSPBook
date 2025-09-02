package com.chinhbean.post.mapper;

import com.chinhbean.post.dto.response.PostResponse;
import com.chinhbean.post.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
