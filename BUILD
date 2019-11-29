load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "accord_main",
    srcs = glob(["**/*.kt"])
)

java_binary(
    name = "accord",
    main_class = "ru.dimsuz.accord.MainKt",
    runtime_deps = [":accord_main"],
)
