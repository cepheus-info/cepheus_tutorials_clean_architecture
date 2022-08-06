package info.cepheus.sample.spring_kafka_sample.application

data class BatchPromotionMessage(
        var transactionId: String? = null,
        var ids: List<String>? = null,
        var changes: List<PromotionDto>? = null,
)

data class PromotionDto(
        var code: String? = null,
        var amount: Double? = null,
)