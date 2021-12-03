package info.cepheus.sample.spring_kafka_sample.application

data class BatchSubsidyDto(
        var ids: List<String>? = null,
        var changes: List<SubsidyDto>? = null,
)

data class SubsidyDto(
        var code: String? = null,
        var amount: Double? = null,
)