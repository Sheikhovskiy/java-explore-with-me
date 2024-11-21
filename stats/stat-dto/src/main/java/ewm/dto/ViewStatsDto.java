package ewm.dto;

import lombok.Data;

@Data
public class ViewStatsDto {

    private String app;   // Название приложения

    private String uri;   // URI, к которому был запрос

    private long hits;    // Количество просмотров (агрегированное значение)
}
