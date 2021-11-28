package ru.nasyrov.alfatest.dto.giphy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDto {

    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("banner_image")
    private String bannerImage;
    @JsonProperty("banner_url")
    private String bannerUrl;
    @JsonProperty("profile_url")
    private String profileUrl;
    @JsonProperty("username")
    private String username;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("is_verified")
    private Boolean isVerified;
    @JsonProperty("website_url")
    private String websiteUrl;
    @JsonProperty("instagram_url")
    private String instagramUrl;
}
