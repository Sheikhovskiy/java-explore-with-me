package ewm.event;

import ewm.exception.ConditionsNotRespected;

public enum EventSortState {

    EVENT_DATE,

    VIEWS;


    public static EventSortState from(String state) {
        for (EventSortState eventSortState : EventSortState.values()) {
            if (eventSortState.name().equals(state)) {
                return eventSortState;
            }
        }
        throw new ConditionsNotRespected(String.format("Ошибка состояние сортировки EVENT: События {} не существует !", state));
    }

}
