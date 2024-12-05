package ewm.subscription;

import ewm.exception.ConditionsNotRespected;

public enum SubscriptionStatus {

    PENDING,

    APPROVED,

    REJECTED,

    APPROVE_REQUEST,

    REJECT_REQUEST;


    public static SubscriptionStatus from(String status) {
        for (SubscriptionStatus subscriptionStatus : SubscriptionStatus.values()) {
            if (subscriptionStatus.name().equals(status)) {
                return subscriptionStatus;
            }
        }
        throw new ConditionsNotRespected(String.format("Ошибка состояние SubscriptionStatus: События {} не существует !", status));
    }

}