package ewm.user.service;

import ewm.event.dto.EventRequestStatusUpdateShortResult;
import ewm.event.model.Event;
import ewm.request.Request;
import ewm.user.AdminUserParam;
import ewm.user.PrivateUserEventParam;
import ewm.user.PrivateUserRequestParam;
import ewm.user.User;

import java.util.List;

public interface UserService {

    User create(User user);

    List<User> getAllByIds(AdminUserParam adminUserParam);

    User delete(AdminUserParam adminUserParam);

    List<Request> getUserRequests(Long userId);

    Request createUserRequest(PrivateUserRequestParam privateUserParam);

    Request cancelUserRequest(PrivateUserRequestParam privateUserParam);

    List<Event> getEventsCreatedByUser(PrivateUserEventParam privateUserEventParam);

    Event createUserEvent(PrivateUserEventParam privateUserEventParam);

    Event getUserEvent(PrivateUserEventParam privateUserEventParam);

    Event updateUserEvent(PrivateUserEventParam privateUserEventParam);

    List<Request> getRequestsOfUserEvent(PrivateUserEventParam privateUserEventParam);

    EventRequestStatusUpdateShortResult updateParticipationRequests(PrivateUserEventParam privateUserEventParam);

}
