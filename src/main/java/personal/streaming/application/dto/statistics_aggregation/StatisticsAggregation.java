package personal.streaming.application.dto.statistics_aggregation;

public record StatisticsAggregation(
        Long contentPostId,
        double contentIncome,
        double adIncome,
        double totalIncome
) {

    public StatisticsAggregation {
        contentIncome = contentIncome / 10;
        adIncome = adIncome / 10;
        totalIncome = totalIncome / 10;
    }
}
