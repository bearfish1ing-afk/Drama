package com.example.demo.domain.friend.elasticsearch.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName = "id_index")
public class IdDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String username;

    private String friendshipStatus;

    @Builder
    public IdDocument(String id, String username,String friendshipStatus) {
        this.id = id;
        this.username = username;
        this.friendshipStatus = friendshipStatus;
    }
}
