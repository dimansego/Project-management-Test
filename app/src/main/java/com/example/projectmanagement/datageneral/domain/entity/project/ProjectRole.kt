package com.example.projectmanagement.datageneral.domain.entity.project

enum class ProjectRole(val value: String) {
    LEADER("leader"),
    MEMBER("member"),
    GUEST("guest");

    companion object {
        fun fromValue(value: String): ProjectRole? = entries.find { it.value == value }
    }
}