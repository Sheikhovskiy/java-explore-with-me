package ewm.event.service;

import ewm.event.AdminEventParam;
import ewm.event.PublicEventParam;
import ewm.event.model.Event;
import ewm.subscription.SubscriptionParam;

import java.util.List;

public interface EventService {

    List<Event> getAllByParameters(AdminEventParam adminEventParam);

    Event update(AdminEventParam adminEventParam);

    List<Event> getAllByPublicParameters(PublicEventParam publicEventParam);

    Event getById(PublicEventParam publicEventParam);

    List<Event> getEventsByFollowedId(SubscriptionParam subscriptionParam);
}
