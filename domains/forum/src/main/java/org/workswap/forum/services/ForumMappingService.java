package org.workswap.forum.services;

import org.workswap.forum.datasource.model.ForumComment;
import org.workswap.forum.datasource.model.ForumPost;
import org.workswap.forum.datasource.model.ForumTopic;
import org.workswap.forum.dto.ForumActivityItemDTO;
import org.workswap.forum.dto.ForumCommentDTO;
import org.workswap.forum.dto.ForumPostDTO;
import org.workswap.forum.dto.ForumPostRequest;
import org.workswap.forum.dto.ForumTopicDTO;

public interface ForumMappingService {
    ForumTopicDTO toRequest(ForumTopic topic);
    ForumTopicDTO toDTO(ForumTopic topic);
    ForumPostRequest toRequest(ForumPost post);
    ForumPostDTO toDTO(ForumPost post);
    ForumCommentDTO toDTO(ForumComment comment);

    ForumActivityItemDTO toActivityItem(ForumTopic topic);
    ForumActivityItemDTO toActivityItem(ForumPost post);
    ForumActivityItemDTO toActivityItem(ForumComment comment);
}
