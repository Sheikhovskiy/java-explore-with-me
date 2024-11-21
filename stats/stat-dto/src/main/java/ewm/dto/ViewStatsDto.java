package ewm.dto;

import lombok.Data;

@Data
public class ViewStatsDto {

    // Название приложения
    private String app;

    // URI, к которому был запрос
    private String uri;

    // Количество просмотров (агрегированное значение)
    private long hits;
}
