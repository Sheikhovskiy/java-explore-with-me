package ewm.event;

import ewm.exception.ConditionsNotRespected;

public enum EventState {

    PENDING,

    PUBLISHED,

    PUBLISH_EVENT,

    REJECT_EVENT,

    SEND_TO_REVIEW,

    CANCEL_REVIEW,

    CANCELED;

    public static EventState from(String state) {
        for (EventState eventState : EventState.values()) {
            if (eventState.name().equals(state)) {
                return eventState;
            }
        }
        throw new ConditionsNotRespected(String.format("Ошибка состояние EVENT: События {} не существует !", state));
    }

    public static String to(EventState eventState) {
        return String.valueOf(eventState);
    }







}
