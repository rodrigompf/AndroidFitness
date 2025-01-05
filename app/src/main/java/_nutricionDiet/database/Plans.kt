package _nutricionDiet.database

sealed class DayPlanItem {
    data class TextPlan(var content: String) : DayPlanItem()
    data class Plan(
        var title: String,
        var description: String,
        var calories: Int
    ) : DayPlanItem()
}
