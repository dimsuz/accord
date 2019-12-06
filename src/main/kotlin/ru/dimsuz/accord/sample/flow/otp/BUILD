load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "sample_otp",
    srcs = glob(["*.kt"]),
    visibility = ["//src/main/kotlin/ru/dimsuz/accord/sample:__pkg__"],
    deps = ["//src/main/kotlin/ru/dimsuz/accord/sample/flow/core:sample_core"],
)
