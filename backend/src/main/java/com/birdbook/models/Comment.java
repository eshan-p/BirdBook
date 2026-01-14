package com.birdbook.models;

import org.bson.types.ObjectId;

import com.birdbook.serializers.ObjectIdSerializer;
import com.birdbook.serializers.ObjectIdDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

public class Comment {

    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId userId;
    
    private String textBody;
    private Date timestamp;

    public Comment() {}

    public Comment(ObjectId userId, String textBody) {
        this.userId = userId;
        this.textBody = textBody;
        this.timestamp = new Date();
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
