package ewm.request;

import ewm.event.dto.EventRequestStatusUpdateResult;
import ewm.event.dto.EventRequestStatusUpdateShortResult;
import ewm.user.dto.ParticipationRequestDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ewm.CommonConstants.COMMON_LOCAL_DATE_TIME_PATTERN;

@UtilityClass
public class RequestMapper {

    public ParticipationRequestDto fromRequestToParticipationRequestDto(Request request) {

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setCreated(fromLocalDateTimeToString(request.getCreated()));
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setId(request.getId());
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setStatus(String.valueOf(request.getStatus()));

        return participationRequestDto;
    }

    public List<ParticipationRequestDto> fromListRequestToListParticipationRequestDto(List<Request> requestList) {

        if (requestList != null) {
            return requestList.stream()
                    .map(RequestMapper::fromRequestToParticipationRequestDto)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public EventRequestStatusUpdateResult fromEventRequestStatusUpdateShortResultToEventRequestStatusUpdateResult(EventRequestStatusUpdateShortResult eventRequestStatusUpdateShortResult) {

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        eventRequestStatusUpdateResult.setConfirmedRequests(RequestMapper.fromListRequestToListParticipationRequestDto(eventRequestStatusUpdateShortResult.getConfirmedRequests()));
        eventRequestStatusUpdateResult.setRejectedRequests(RequestMapper.fromListRequestToListParticipationRequestDto((eventRequestStatusUpdateShortResult.getRejectedRequests())));

        return eventRequestStatusUpdateResult;
    }





    private String fromLocalDateTimeToString(LocalDateTime localDateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_LOCAL_DATE_TIME_PATTERN);
        LocalDateTime dateTime = LocalDateTime.from(localDateTime);
        return dateTime.format(formatter);
    }

}
