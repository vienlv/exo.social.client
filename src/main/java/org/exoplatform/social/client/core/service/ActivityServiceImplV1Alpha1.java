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
package org.exoplatform.social.client.core.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.exoplatform.social.client.api.SocialClientLibException;
import org.exoplatform.social.client.api.UnsupportedMethodException;
import org.exoplatform.social.client.api.auth.NotFoundException;
import org.exoplatform.social.client.api.common.RealtimeListAccess;
import org.exoplatform.social.client.api.model.RestActivity;
import org.exoplatform.social.client.api.model.RestComment;
import org.exoplatform.social.client.api.model.RestIdentity;
import org.exoplatform.social.client.api.model.RestLike;
import org.exoplatform.social.client.api.net.SocialHttpClient.POLICY;
import org.exoplatform.social.client.api.net.SocialHttpClientException;
import org.exoplatform.social.client.api.service.ActivityService;
import org.exoplatform.social.client.api.service.QueryParams;
import org.exoplatform.social.client.api.service.ServiceException;
import org.exoplatform.social.client.core.model.RestActivityImpl;
import org.exoplatform.social.client.core.model.RestCommentImpl;
import org.exoplatform.social.client.core.model.RestLikeImpl;
import org.exoplatform.social.client.core.service.ActivitiesRealtimeListAccessV1Alpha1.ActivityType;
import org.exoplatform.social.client.core.util.SocialJSONDecodingSupport;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import static org.exoplatform.social.client.core.util.SocialHttpClientSupport.buildCommonRestPathFromContext;
import static org.exoplatform.social.client.core.util.SocialHttpClientSupport.executeGet;
import static org.exoplatform.social.client.core.util.SocialHttpClientSupport.executePost;
import static org.exoplatform.social.client.core.util.SocialHttpClientSupport.getContent;
import static org.exoplatform.social.client.core.util.SocialHttpClientSupport.handleError;

/**
 * Implementation of {@link org.exoplatform.social.client.api.service.ActivityService}.
 *
 * @author <a href="http://hoatle.net">hoatle (hoatlevan at gmail dot com)</a>
 * @since Jun 28, 2011
 */
public class ActivityServiceImplV1Alpha1 extends ServiceBase<RestActivity, ActivityService<RestActivity>> implements ActivityService<RestActivity> {
  private static final String BASE_URL = buildCommonRestPathFromContext(true);

  /**
   * {@inheritDoc}
   */
  @Override
  public RestActivity create(RestActivity newInstance) throws SocialClientLibException {
    final String POST_ACTIVITY_REQUEST_URL = BASE_URL+"activity.json";
      try{
        HttpResponse response = executePost(POST_ACTIVITY_REQUEST_URL, POLICY.BASIC_AUTH, newInstance);
        handleError(response);
        String responseContent = getContent(response);
        RestActivity restActivity = SocialJSONDecodingSupport.parser(RestActivityImpl.class, responseContent);
        return restActivity;
      } catch (SocialHttpClientException e) {
        throw new ServiceException(ActivityServiceImplV1Alpha1.class,e.getMessage(),null);
      } catch (ParseException e) {
        throw new ServiceException(ActivityServiceImplV1Alpha1.class,e.getMessage(),null);
      } catch (SocialClientLibException e) {
        if(e.getCause() instanceof NotFoundException){
          throw new ServiceException(ActivityServiceImplV1Alpha1.class,e.getMessage(),null);
        } else {
          throw e;
        }
      }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RestActivity get(String uuid) throws SocialClientLibException {
    final String GET_ACTIVITY_REQUEST_URL = BASE_URL+"activity/"+uuid+".json";
      try{
        HttpResponse response = executeGet(GET_ACTIVITY_REQUEST_URL, POLICY.BASIC_AUTH);
        handleError(response);
        String responseContent = getContent(response);
        RestActivity restActivity = SocialJSONDecodingSupport.parser(RestActivityImpl.class, responseContent);
        return restActivity;
      } catch (SocialHttpClientException e) {
        throw new ServiceException(ActivityServiceImplV1Alpha1.class,e.getMessage(),null);
      } catch (ParseException e) {
        throw new ServiceException(ActivityServiceImplV1Alpha1.class,e.getMessage(),null);
      }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RestActivity update(RestActivity existingInstance) throws SocialClientLibException {
    throw new ServiceException(ActivityServiceImplV1Alpha1.class,"Do Not Support",null);
  }

  /**
   * {@inheritDoc}
   * @throws org.exoplatform.social.client.api.SocialClientLibException
   */
  @Override
  public RestActivity delete(RestActivity existingInstance) throws SocialClientLibException {
    final String DELETE_ACTIVITY_REQUEST_URL = BASE_URL+"activity/destroy/"+existingInstance.getId()+".json";
    try{
      HttpResponse response = executePost(DELETE_ACTIVITY_REQUEST_URL, POLICY.BASIC_AUTH);
      handleError(response);
      String responseContent = getContent(response);
      RestActivity restActivity = SocialJSONDecodingSupport.parser(RestActivityImpl.class, responseContent);
      return restActivity;
    } catch (SocialHttpClientException e) {
      throw new ServiceException(ActivityServiceImplV1Alpha1.class,e.getMessage(),null);
    } catch (ParseException e) {
      throw new ServiceException(ActivityServiceImplV1Alpha1.class,e.getMessage(),null);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public RealtimeListAccess<RestActivity> getActivityStream(RestIdentity restIdentity) throws SocialClientLibException {
    return new ActivitiesRealtimeListAccessV1Alpha1(restIdentity, ActivityType.ACTIVITY_STREAM);
  }

  @Override
  public RealtimeListAccess<RestActivity> getSpacesActivityStream(RestIdentity userRestIdentity) throws SocialClientLibException {
    return new ActivitiesRealtimeListAccessV1Alpha1(userRestIdentity, ActivityType.USER_SPACE_ACTIVITIES);
  }

  @Override
  public RealtimeListAccess<RestActivity> getConnectionsActivityStream(RestIdentity userRestIdentity) throws SocialClientLibException {
    return new ActivitiesRealtimeListAccessV1Alpha1(userRestIdentity, ActivityType.CONNECTIONS_ACTIVITIES);
  }

  @Override
  public RealtimeListAccess<RestActivity> getFeedActivityStream(RestIdentity userRestIdentity) throws SocialClientLibException {
    return new ActivitiesRealtimeListAccessV1Alpha1(userRestIdentity, ActivityType.ACTIVITY_FEED);
  }


  /**
   * {@inheritDoc}
   * @throws org.exoplatform.social.client.api.SocialClientLibException
   */
  @Override
  public RestComment createComment(RestActivity existingRestActivity, RestComment newRestComment) throws SocialClientLibException {
    final String CREATE_COMMENT_REQUEST_URL = BASE_URL+"activity/"+ existingRestActivity.getId()+"/comment.json";
      try{
        HttpResponse response = executePost(CREATE_COMMENT_REQUEST_URL, POLICY.BASIC_AUTH, newRestComment);
        handleError(response);
        String responseContent = getContent(response);
        RestComment restComment = SocialJSONDecodingSupport.parser(RestCommentImpl.class, responseContent);
        return restComment;
      } catch (SocialHttpClientException e) {
        throw new ServiceException(ActivityServiceImplV1Alpha1.class,e.getMessage(),null);
      } catch (ParseException e) {
        throw new ServiceException(ActivityServiceImplV1Alpha1.class,e.getMessage(),null);
      }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RestComment getComment(String commentId) throws SocialClientLibException {
    throw new ServiceException(ActivityServiceImplV1Alpha1.class, "Not Supported",null);
//    final String GET_ACTIVITY_REQUEST_URL = BASE_URL+commentId+".json";
//    try {
//      HttpResponse response = SocialHttpClientSupport.executeGet(GET_ACTIVITY_REQUEST_URL,POLICY.BASIC_AUTH);
//      int statusCode = response.getStatusLine().getStatusCode();
//      if(statusCode != ServiceException.HTTP_OK){
//          throw new ServiceException(ActivityServiceImplV1Alpha1.class,"invalid response: Status " + statusCode, null);
//      } else {
//        String responseContent = SocialHttpClientSupport.getContent(response);
//        try{
//          RestComment comment = SocialJSONDecodingSupport.parser(RestComment.class, responseContent);
//          return comment;
//        } catch (Exception e) {
//          throw new ServiceException(ActivityServiceImplV1Alpha1.class,"invalid response",null);
//        }
//      }
//    } catch (Exception e) {
//      throw new ServiceException(ActivityServiceImplV1Alpha1.class, "There's error when execute request",null);
//    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RestComment deleteComment(RestComment existingRestComment) throws SocialClientLibException {
    final String DELETE_COMMENT_REQUEST_URL = BASE_URL+"activity/"+ existingRestComment.getActivityId() + "/comment/destroy/" +
                                            existingRestComment.getId() + ".json";
    try{
      HttpResponse response = executePost(DELETE_COMMENT_REQUEST_URL, POLICY.BASIC_AUTH);
      handleError(response);
      String responseContent = getContent(response);
      RestComment restComment = SocialJSONDecodingSupport.parser(RestCommentImpl.class, responseContent);
      return restComment;
    } catch (SocialHttpClientException e) {
      throw new ServiceException(ActivityServiceImplV1Alpha1.class,"invalid response",null);
    } catch (ParseException e) {
      throw new ServiceException(ActivityServiceImplV1Alpha1.class,"invalid response",null);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RestLike like(RestActivity existingRestActivity) throws SocialClientLibException {
    final String LIKE_ACTIVITY_REQUEST_URL = BASE_URL+"activity/"+ existingRestActivity.getId()+"/like.json";
    try{
      HttpResponse response = executePost(LIKE_ACTIVITY_REQUEST_URL, POLICY.BASIC_AUTH);
      handleError(response);
      String responseContent = getContent(response);

      JSONObject responseJson = (JSONObject)JSONValue.parse(responseContent);
      if((Boolean) responseJson.get("like")){
        return new RestLikeImpl(existingRestActivity.getId(), null);
      } else {
        throw new ServiceException(ActivityServiceImplV1Alpha1.class,"invalid response",null);
      }
    } catch (IOException e) {
      throw new ServiceException(ActivityServiceImplV1Alpha1.class,"invalid response",null);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public RestLike unlike(RestActivity existingRestActivity) throws SocialClientLibException {
    final String LIKE_ACTIVITY_REQUEST_URL = BASE_URL+"activity/"+ existingRestActivity.getId()+"/like/destroy.json";
    try{
      HttpResponse response = executePost(LIKE_ACTIVITY_REQUEST_URL, POLICY.BASIC_AUTH);
      String responseContent = getContent(response);
      handleError(response);
      JSONObject responseJson = (JSONObject)JSONValue.parse(responseContent);

      if(!(Boolean) responseJson.get("like")){
        return new RestLikeImpl(existingRestActivity.getId(), null);
      } else {
        throw new ServiceException(ActivityServiceImplV1Alpha1.class,"invalid response",null);
      }
    } catch (IOException e){
      throw new ServiceException(e.getMessage(),e);
    }
  }

  @Override
  public RealtimeListAccess<RestActivity> getActivityStream(RestIdentity restIdentity, QueryParams queryParams)
                                                                                            throws SocialClientLibException {
    throw new UnsupportedMethodException("Not Supported Yet.");
  }

  @Override
  public RestActivity create(RestActivity newActivity, QueryParams queryParams) throws SocialClientLibException {
    throw new UnsupportedMethodException("Not Supported Yet.");
  }

  @Override
  public RealtimeListAccess<RestActivity> getSpacesActivityStream(RestIdentity restIdentity, QueryParams queryParams)
                                                                                           throws SocialClientLibException {
    throw new UnsupportedMethodException("Not Supported Yet.");
  }

  @Override
  public RealtimeListAccess<RestActivity> getConnectionsActivityStream(RestIdentity restIdentity, QueryParams queryParams)
      throws SocialClientLibException {
    throw new UnsupportedMethodException("Not Supported Yet.");
  }

  @Override
  public RealtimeListAccess<RestActivity> getFeedActivityStream(
      RestIdentity restIdentity, QueryParams queryParams)
      throws SocialClientLibException {
    throw new UnsupportedMethodException("Not Supported Yet.");
  }

  @Override
  public RestActivity get(String activityId, QueryParams queryParams)
      throws SocialClientLibException {
    throw new UnsupportedMethodException("Not Supported Yet.");
  }
}
