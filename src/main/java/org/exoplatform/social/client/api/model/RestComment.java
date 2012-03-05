/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.client.api.model;

import org.exoplatform.social.client.api.util.SocialJSONDecodingSupport;
import org.json.simple.parser.ParseException;

/**
 * The RestComment model.
 *
 * @author <a href="http://hoatle.net">hoatle (hoatlevan at gmail dot com)</a>
 * @since May 19, 2011
 */
public class RestComment extends Model {

  /**
   * The fields that represent the RestComment object in json form.
   *
   * <p>
   * All of the fields that comments can have.
   * </p>
   *
   */
  public static enum Field {
    ID("id"),
    /** the json field for userId. */
    IDENTITY_ID("identityId"),
    /** the json field for activity. */
    ACTIVITY("activity"),
     /** the json field for streamTitle. */
    TEXT("text"),
    /** the json field for postedTime. */
    POSTED_TIME("postedTime"),
    /** the json field for createdAt. */
    CREATED_AT("createdAt"),
    /** the identity who comment the activity */
    POSTER_IDENTITY("posterIdentity"),
    /** the json field for activity. */
    ACTIVITY_ID("activityId"),;

    /**
     * The json field that the instance represents.
     */
    private final String jsonString;

    /**
     * create a field base on the a json element.
     *
     * @param jsonString the name of the element
     */
    private Field(String jsonString) {
      this.jsonString = jsonString;
    }

    /**
     * emit the field as a json element.
     *
     * @return the field name
     */
    @Override
    public String toString() {
      return jsonString;
    }
  }

  /**
   * Constructor without any params.
   */
  public RestComment() {

  }
  
  /**
   * Constructor.
   *
   * @param id         the comment id
   * @param identityId the identity id
   * @param postedTime the posted time
   * @param createdAt
   */
  public RestComment(String id, String identityId, Long postedTime, String createdAt) {
    setId(id);
    setIdentityId(identityId);
    setPostedTime(postedTime);
    setCreatedAt(createdAt);
  }
  
  /**
   * Constructor.
   *
   * @param id         the comment id
   * @param identityId the identity id
   * @param activity the activity
   * @param postedTime the posted time
   */
  public RestComment(String id, String identityId, RestActivity activity, Long postedTime, String createdAt) {
    setId(id);
    setIdentityId(identityId);
    setActivity(activity);
    setPostedTime(postedTime);
    setCreatedAt(createdAt);
  }
  
  /**
   * Gets the comment id.
   *
   * @return the comment id
   */
  public String getId() {
    return getFieldAsString(Field.ID.toString());
  }

  /**
   * Sets the comment id.
   *
   * @param id the comment id
   */
  public void setId(String id) {
    setField(Field.ID.toString(), id);
  }

  /**
   * Gets identity id who posted this comment.
   *
   * @return the identity id
   * @deprecated only use with v1-alpha1
   */
  public String getIdentityId() {
    return getFieldAsString(Field.IDENTITY_ID.toString());
  }

  /**
   * Sets identity id who posted this comment.
   *
   * @param identityId the identity id
   * @deprecated only use with v1-alpha1
   */
  public void setIdentityId(String identityId) {
    setField(Field.IDENTITY_ID.toString(), identityId);
  }

  /**
   * Gets the activity which is associated with this comment.
   *
   * @return the activity.
   */
  public RestActivity getActivity() {
    return (RestActivity) getField(Field.ACTIVITY.toString());
  }

  /**
   * Gets the activity id which is associated with this comment.
   *
   * @return the activity id.
   */
  public String getActivityId() {
    if (this.getActivity() != null) {
      return this.getActivity().getId();
    }
    return null;
  }
  
  /**
   * Sets the activity which is associated with this comment.
   *
   * @param activity the activity
   */
  public void setActivity(RestActivity activity) {
    setField(Field.ACTIVITY.toString(), activity);
  }

  /**
   * Gets the comment content.
   *
   * @return the comment content.
   */
  public String getText() {
    return getFieldAsString(Field.TEXT.toString());
  }

  /**
   * Sets the comment content.
   *
   * @param content the comment content
   */
  public void setText(String content) {
    setField(Field.TEXT.toString(), content);
  }

  /**
   * Gets the posted time of this comment as timestamp value.
   *
   * @return the posted time of this comment
   */
  public Long getPostedTime() {
    return (Long) getField(Field.POSTED_TIME.toString());
  }

  /**
   * Sets the posted time of this comment as timestamp value.
   *
   * @param postedTime the posted time of this comment.
   */
  public void setPostedTime(Long postedTime) {
    setField(Field.POSTED_TIME.toString(), postedTime);
  }

  /**
   * Gets the created at of this comment as a time string value.
   *
   * @return the time string value
   */
  public String getCreatedAt() {
    return getFieldAsString(Field.CREATED_AT.toString());
  }

  /**
   * Sets the created at of this comment as a time string value.
   *
   * @param createdAt the time string value
   */
  public void setCreatedAt(String createdAt) {
    setField(Field.CREATED_AT.toString(), createdAt);
  }

  /**
   * Gets the identity who commented.
   *
   * This must be lazy loading for better performance.
   *
   * @return the identity
   * @deprecated only use with v1-alpha1
   */
  public RestIdentity getIdentity() {
    String posterIdentityJson = getFieldAsString(Field.POSTER_IDENTITY.toString());
    try {
      return posterIdentityJson == null ? new RestIdentity() : SocialJSONDecodingSupport.parser(RestIdentity.class, posterIdentityJson);
    } catch (ParseException pex) {
      return new RestIdentity();
    }
  }
  
  /**
   * Gets the identity who commented.
   * if poster identity is null this will be lazy loading the identity from Rest.
   * @return
   */
  public RestIdentity getPosterIdentity() {
    String posterIdentityJson = getFieldAsString(Field.POSTER_IDENTITY.toString());
    try {
      return posterIdentityJson == null ? new RestIdentity() : SocialJSONDecodingSupport.parser(RestIdentity.class, posterIdentityJson);
    } catch (ParseException pex) {
      return new RestIdentity();
    }
  }
  
  /**
   * Sets the identity who commented.
   * @param restIdentity
   * @return
   */
  public void setPosterIdentity(RestIdentity restIdentity) {
    setField(Field.POSTER_IDENTITY.toString(), restIdentity);
  }
}
