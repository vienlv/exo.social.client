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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.social.client.api.ClientServiceFactory;
import org.exoplatform.social.client.api.common.RealtimeListAccess;
import org.exoplatform.social.client.api.model.RestActivity;
import org.exoplatform.social.client.api.model.RestComment;
import org.exoplatform.social.client.api.model.RestIdentity;
import org.exoplatform.social.client.api.model.RestLike;
import org.exoplatform.social.client.api.service.ActivityService;
import org.exoplatform.social.client.api.service.IdentityService;
import org.exoplatform.social.client.api.service.ServiceException;
import org.exoplatform.social.client.core.ClientServiceFactoryHelper;
import org.exoplatform.social.client.core.model.RestActivityImpl;
import org.exoplatform.social.client.core.model.RestCommentImpl;
import org.exoplatform.social.client.core.net.AbstractClientTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * Unit Test for {@link org.exoplatform.social.client.api.service.ActivityService}'s implementation.
 *
 * @author <a href="http://hoatle.net">hoatle (hoatlevan at gmail dot com)</a>
 * @since Jul 3, 2011
 */
public class ActivityServiceIT extends AbstractClientTest {

  private ActivityService<RestActivity> activityService;
  private IdentityService<RestIdentity> identityService;

  private List<RestActivity> tearDownActivityList;

  @BeforeMethod
  @Override
  public void setUp() {
    super.setUp();
    ClientServiceFactory clientServiceFactory = ClientServiceFactoryHelper.getClientServiceFactory();
    activityService = clientServiceFactory.createActivityService();
    identityService = clientServiceFactory.createIdentityService();
    tearDownActivityList = new ArrayList<RestActivity>();
  }

  @AfterMethod
  @Override
  public void tearDown() {
    startSessionAs("demo", "gtn");
    for (RestActivity activity: tearDownActivityList) {
      activityService.delete(activity);
    }
    startSessionAsAnonymous();
    tearDownActivityList = null;
    activityService = null;
    identityService = null;
    super.tearDown();
  }

  /**
   * Tests the case when not authenticated
   */
  @Test
  public void shouldBeForbidden() {
    try {
      activityService.get("notfound");
      fail("Expecting ServiceException from ActivityService#get(String)");
    } catch (ServiceException se) {
      //expected
    }
    RestActivity activity = new RestActivityImpl();
    activity.setTitle("Hello There");
    try {
      activityService.create(activity);
      fail("Expecting ServiceException from ActivityService#create(RestActivity)");
    } catch (ServiceException se) {
      //expected
    }
    try {
      activityService.update(activity);
      fail("Expecting ServiceException from ActivityService#update(RestActivity)");
    } catch (ServiceException se) {
      //expected
    }

    //create a activity to demo's stream
    startSessionAs("demo", "gtn");
    RestActivity demoActivity = createActivities(1).get(0);
    startSessionAsAnonymous();

    RestComment comment = new RestCommentImpl();
    comment.setText("comment");

    try {
      activityService.createComment(demoActivity, comment);
      fail("Expecting ServiceException from ActivityService#createComment(RestActivity, RestComment)");
    } catch (ServiceException se) {

    }
  }

  @Test
  public void shouldBeUnsupported() {
    //TODO
  }

  @Test
  public void shouldNotFound() {
    //TODO
  }

  /**
   * Test {@link ActivityService#create(Object)} 
   */
  @Test
  public void shouldCreate() {
    startSessionAs("demo", "gtn");
    String demoIdentityId = identityService.getIdentityId("organization", "demo");

    RestActivity restActivityToCreate = new RestActivityImpl();
    restActivityToCreate.setTitle("Hello PhuongLM!!!");
    RestActivity restActivityResult = activityService.create(restActivityToCreate);

    assertThat(restActivityResult.getId(), notNullValue());

    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));

    tearDownActivityList.add(restActivityResult);
  }

  /**
   * Test {@link ActivityService#get(String)}
   */
  @Test
  public void shouldGet() {
    startSessionAs("demo", "gtn");
    String demoIdentityId = identityService.getIdentityId("organization", "demo");

    RestActivity restActivityToCreate = new RestActivityImpl();
    restActivityToCreate.setTitle("Hello PhuongLM!!!");
    RestActivity restActivityResult = activityService.create(restActivityToCreate);

    assertThat(restActivityResult.getId(), notNullValue());

    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));

    activityService.like(restActivityResult);

    String createdActivityId = restActivityResult.getId();
    restActivityResult = activityService.get(createdActivityId);

    assertThat(restActivityResult.getId(), equalTo(createdActivityId));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(1));
    RestLike restLike = restActivityResult.getLikes().get(0);
    assertThat(restLike.getIdentityId() , equalTo(demoIdentityId));

    RestComment comment = new RestCommentImpl();
    comment.setText("hello");

    activityService.createComment(restActivityResult, comment);

    restActivityResult = activityService.get(createdActivityId);
    assertThat(restActivityResult.getTotalNumberOfComments(), equalTo(1));
    assertThat(restActivityResult.getAvailableComments().get(0).getText(), equalTo("hello"));
    
    tearDownActivityList.add(restActivityResult);
  }

  /**
   * Test {@link ActivityService#update(Object)}
   */
  @Test
  public void shouldNotUpdate() {
    startSessionAs("demo", "gtn");
    String demoIdentityId = identityService.getIdentityId("organization", "demo");

    RestActivity restActivityToCreate = new RestActivityImpl();
    restActivityToCreate.setTitle("Hello PhuongLM!!!");
    RestActivity restActivityResult = activityService.create(restActivityToCreate);

    String activityId = restActivityResult.getId();
    
    assertThat(restActivityResult.getId(), notNullValue());

    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));

    restActivityResult.setTitle("new activity title");
    
    try {
      activityService.update(restActivityResult);
      fail("Expecting ServiceException from ActivityService#update(Object)");
    } catch (ServiceException e) {
      //expected
    }
    
    restActivityResult = activityService.get(activityId);
    
    tearDownActivityList.add(restActivityResult);
  }

  /**
   * Test {@link ActivityService#delete(Object)}
   */
  @Test
  public void shouldDelete() {
    startSessionAs("demo", "gtn");
    String demoIdentityId = identityService.getIdentityId("organization", "demo");

    RestActivity restActivityToCreate = new RestActivityImpl();
    restActivityToCreate.setTitle("Hello PhuongLM!!!");
    RestActivity restActivityResult = activityService.create(restActivityToCreate);

    assertThat(restActivityResult.getId(), notNullValue());

    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));

    String createdActivityId = restActivityResult.getId();
    
    RestComment comment = new RestCommentImpl();
    comment.setText("hello");

    activityService.createComment(restActivityResult, comment);

    restActivityResult = activityService.get(createdActivityId);
    assertThat(restActivityResult.getTotalNumberOfComments(), equalTo(1));
    assertThat(restActivityResult.getAvailableComments().get(0).getText(), equalTo("hello"));

    activityService.delete(restActivityResult);

    try {
      restActivityResult = activityService.get(createdActivityId);
      fail("failed to check if RestActivity deleted.");
    } catch (ServiceException e) {
      //expected
    }
  }

  @Test
  public void testCreateGetDeleteActivity() {
    startSessionAs("demo", "gtn");
    String demoIdentityId = identityService.getIdentityId("organization", "demo");

    RestActivity restActivityToCreate = new RestActivityImpl();
    restActivityToCreate.setTitle("Hello PhuongLM!!!");
    RestActivity restActivityResult = activityService.create(restActivityToCreate);

    assertThat(restActivityResult.getId(), notNullValue());

    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));

    activityService.like(restActivityResult);

    String createdActivityId = restActivityResult.getId();
    restActivityResult = activityService.get(createdActivityId);

    assertThat(restActivityResult.getId(), equalTo(createdActivityId));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(1));
    RestLike restLike = restActivityResult.getLikes().get(0);
    assertThat(restLike.getIdentityId() , equalTo(demoIdentityId));

    RestComment comment = new RestCommentImpl();
    comment.setText("hello");

    activityService.createComment(restActivityResult, comment);

    restActivityResult = activityService.get(createdActivityId);
    assertThat(restActivityResult.getTotalNumberOfComments(), equalTo(1));
    assertThat(restActivityResult.getAvailableComments().get(0).getText(), equalTo("hello"));

    activityService.delete(restActivityResult);

    try {
      restActivityResult = activityService.get(createdActivityId);
      fail("failed to check if RestActivity deleted.");
    } catch (ServiceException e) {
      //expected
    }
  }

  /**
   * Test {@link ActivityService#getActivityStream(RestIdentity)}
   */
  @Test
  public void testGetActivityStream() {
    startSessionAs("demo", "gtn");

    String demoIdentityId = identityService.getIdentityId("organization", "demo");
    RestIdentity demoIdentity = identityService.get(demoIdentityId);

    int i = 1;
    createActivities(i);
    RealtimeListAccess<RestActivity> activityListAccess = activityService.getActivityStream(demoIdentity);
    RestActivity[] resultArray = activityListAccess.load(0, i);
    assertThat(resultArray.length, equalTo(1));
    for(int j = 0; i < j; i++) {
      assertThat(activityListAccess.load(j, j+1)[0].getTitle(), equalTo(new Integer(j).toString()));
    }
    
    // TODO: Cause the Rest API don't provide relationship and space interface so
    // we cannot create data for test connectionActivityStream and spaceActivitySteam.
    // Improve in next version
  }

  
  /**
   * Test {@link ActivityService#getSpacesActivityStream(RestIdentity)}
   */
  @Test
  public void testGetSpaceActivityStream() {
    //TODO
  }

  /**
   * Test {@link ActivityService#getConnectionsActivityStream(RestIdentity)}
   */
  @Test
  public void testGetConnectionsActivityStream() {
    startSessionAs("demo", "gtn");

    String demoIdentityId = identityService.getIdentityId("organization", "demo");
    RestIdentity demoIdentity = identityService.get(demoIdentityId);

    int i = 1;
    createActivities(i);
    RealtimeListAccess<RestActivity> activityListAccess = activityService.getConnectionsActivityStream(demoIdentity);
    RestActivity[] resultArray = activityListAccess.load(0, i);
    assertThat(resultArray.length, equalTo(0));
    //TODO Need to create connections between identities to test get connection activity stream.
  }
  
  /**
   * Test {@link ActivityService#getFeedActivityStream(RestIdentity)}
   */
  @Test
  public void testGetFeedActivityStream() {
    startSessionAs("demo", "gtn");

    String demoIdentityId = identityService.getIdentityId("organization", "demo");
    RestIdentity demoIdentity = identityService.get(demoIdentityId);

    int i = 1;
    createActivities(i);
    RealtimeListAccess<RestActivity> activityListAccess = activityService.getFeedActivityStream(demoIdentity);
    RestActivity[] resultArray = activityListAccess.load(0, i);
    assertThat(resultArray.length, equalTo(1));
    for(int j = 0; i < j; i++) {
      assertThat(activityListAccess.load(j, j+1)[0].getTitle(), equalTo(new Integer(j).toString()));
    }
    //TODO Need to create space, connections between identities to test get feed activity stream.
  }
  
  /**
   * Test {@link ActivityService#createComment(Object, RestComment)}
   */
  @Test
  public void testCreateComment() {
    startSessionAs("demo", "gtn");
    String demoIdentityId = identityService.getIdentityId("organization", "demo");

    RestActivity restActivityToCreate = new RestActivityImpl();
    restActivityToCreate.setTitle("Hello PhuongLM!!!");
    RestActivity restActivityResult = activityService.create(restActivityToCreate);

    assertThat(restActivityResult.getId(), notNullValue());
    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));

    RestComment comment = new RestCommentImpl();
    comment.setText("hello");

    activityService.createComment(restActivityResult, comment);

    restActivityResult = activityService.get(restActivityResult.getId());
    assertThat(restActivityResult.getTotalNumberOfComments(), equalTo(1));
    assertThat(restActivityResult.getAvailableComments().get(0).getText(), equalTo("hello"));

    activityService.delete(restActivityResult);
  }
  
  /**
   * Test {@link ActivityService#deleteComment(RestComment)}
   */
  @Test
  public void testDeleteComment() {
    startSessionAs("demo", "gtn");
    String demoIdentityId = identityService.getIdentityId("organization", "demo");

    RestActivity restActivityToCreate = new RestActivityImpl();
    restActivityToCreate.setTitle("Hello PhuongLM!!!");
    RestActivity restActivityResult = activityService.create(restActivityToCreate);

    assertThat(restActivityResult.getId(), notNullValue());
    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));

    RestComment comment = new RestCommentImpl();
    comment.setText("hello");

    RestComment commentResult = activityService.createComment(restActivityResult, comment);

    restActivityResult = activityService.get(restActivityResult.getId());
    assertThat(restActivityResult.getTotalNumberOfComments(), equalTo(1));
    assertThat(restActivityResult.getAvailableComments().get(0).getText(), equalTo("hello"));

    activityService.deleteComment(commentResult);
    
    restActivityResult = activityService.get(restActivityResult.getId());
    assertThat(restActivityResult.getTotalNumberOfComments(), equalTo(0));
    
    activityService.delete(restActivityResult);
  }
  
  /**
   * Test {@link ActivityService#getComment(String)}
   */
  @Test
  public void testGetComment() {
    startSessionAs("demo", "gtn");
    String demoIdentityId = identityService.getIdentityId("organization", "demo");

    RestActivity restActivityToCreate = new RestActivityImpl();
    restActivityToCreate.setTitle("Hello PhuongLM!!!");
    RestActivity restActivityResult = activityService.create(restActivityToCreate);

    assertThat(restActivityResult.getId(), notNullValue());
    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));

    RestComment comment = new RestCommentImpl();
    comment.setText("hello");

    RestComment commentResult = activityService.createComment(restActivityResult, comment);

    restActivityResult = activityService.get(restActivityResult.getId());
    assertThat(restActivityResult.getTotalNumberOfComments(), equalTo(1));
    assertThat(restActivityResult.getAvailableComments().get(0).getText(), equalTo("hello"));
    
    try {
      commentResult = activityService.getComment(commentResult.getId());
      fail("Expecting ServiceException from ActivityService#create(RestActivity): Not supported");
    } catch (ServiceException e) {
      //expected
    }
    
    activityService.delete(restActivityResult);
  }
  
  /**
   * Test {@link ActivityService#like(Object)}
   */
  @Test
  public void testLike() {
    startSessionAs("demo", "gtn");
    String demoIdentityId = identityService.getIdentityId("organization", "demo");

    RestActivity restActivityToCreate = new RestActivityImpl();
    restActivityToCreate.setTitle("Hello PhuongLM!!!");
    RestActivity restActivityResult = activityService.create(restActivityToCreate);

    assertThat(restActivityResult.getId(), notNullValue());

    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));

    activityService.like(restActivityResult);

    String createdActivityId = restActivityResult.getId();
    restActivityResult = activityService.get(createdActivityId);

    assertThat(restActivityResult.getId(), equalTo(createdActivityId));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(1));
    RestLike restLike = restActivityResult.getLikes().get(0);
    assertThat(restLike.getIdentityId() , equalTo(demoIdentityId));
    
    activityService.delete(restActivityResult);
  }
  
  /**
   * Test {@link ActivityService#unlike(Object)}
   */
  @Test
  public void testUnlike() {
    startSessionAs("demo", "gtn");
    String demoIdentityId = identityService.getIdentityId("organization", "demo");

    RestActivity restActivityToCreate = new RestActivityImpl();
    restActivityToCreate.setTitle("Hello PhuongLM!!!");
    RestActivity restActivityResult = activityService.create(restActivityToCreate);

    assertThat(restActivityResult.getId(), notNullValue());

    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));

    activityService.like(restActivityResult);

    String createdActivityId = restActivityResult.getId();
    restActivityResult = activityService.get(createdActivityId);

    assertThat(restActivityResult.getId(), equalTo(createdActivityId));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(1));
    RestLike restLike = restActivityResult.getLikes().get(0);
    assertThat(restLike.getIdentityId() , equalTo(demoIdentityId));
    
    activityService.unlike(restActivityResult);
    
    restActivityResult = activityService.get(createdActivityId);
    assertThat(restActivityResult.getId(), notNullValue());

    assertThat(restActivityResult.getTitle(), equalTo("Hello PhuongLM!!!"));
    assertThat(restActivityResult.getIdentityId(), equalTo(demoIdentityId));
    assertThat(restActivityResult.getLikes().size(), equalTo(0));
    
    activityService.delete(restActivityResult);
  }
  
  /**
   * Creates activities.
   * 
   * @param numberOfActivity
   * @return
   */
  private List<RestActivity> createActivities(int numberOfActivity) {
    List<RestActivity> createdActivityList = new ArrayList<RestActivity>();
    for (int i = 0; i < numberOfActivity; i++) {
      RestActivity restActivityToCreate = new RestActivityImpl();
      restActivityToCreate.setTitle("test " + i);
      RestActivity createdActivity = activityService.create(restActivityToCreate);
      createdActivityList.add(createdActivity);
      tearDownActivityList.add(createdActivity);
    }
    return createdActivityList;
  }
}
