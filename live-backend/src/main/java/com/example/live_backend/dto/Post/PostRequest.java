package com.example.live_backend.dto.Post;

import lombok.Data;
import java.util.List;

@Data
public class PostRequest {
    private String caption;

    private List<String> imageUrl;

    private boolean isPublic = true;

    private String location;
}
