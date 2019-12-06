load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "sample_core",
    srcs = glob(["*.kt"]),
    visibility = ["//src/main/kotlin/ru/dimsuz/accord/sample:__subpackages__"],
    deps = ["//src/main:accord_lib"],
)
