load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "sample_main",
    srcs = glob(["*.kt"]),
    visibility = ["//:__pkg__"],
    deps = [
        "//src/main:accord_lib",
        "//src/main/kotlin/ru/dimsuz/accord/sample/flow/login:sample_login",
        "//src/main/kotlin/ru/dimsuz/accord/sample/flow/otp:sample_otp",
    ],
)
