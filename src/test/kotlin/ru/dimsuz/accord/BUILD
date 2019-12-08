load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_test")

kt_jvm_test(
    name = "accord_test",
    srcs = glob(["*.kt"]),
    test_class = "ru.dimsuz.accord.DslConfigurationTest",
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:com_google_truth_truth",
        "@maven//:junit_junit",
    ],
)
