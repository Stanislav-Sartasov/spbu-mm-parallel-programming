# ThreadPool

This project implements a ThreadPool that supports task enqueuing with work-sharing and work-stealing strategies and also continuation chaining.

## Building the Project

To build the project: `make`

## Running Tests

To build and run the tests, use: `make tests`

Note: The code is compiled with the thread sanitizer to catch data races and other threading-related issues.
To ensure the thread sanitizer works correctly, you might need to disable address space layout randomization (ASLR) by running:

```
echo 0 > /proc/sys/kernel/randomize_va_space
```

## Dependencies

This project relies on the Google Test library for its testing framework. Follow the steps below to install Google Test:

```bash
    cd $HOME
    sudo apt-get install -y libgtest-dev cmake
    mkdir -p $HOME/build
    cd $HOME/build
    sudo cmake /usr/src/googletest/googletest
    sudo make
    sudo cp lib/libgtest* /usr/lib/
    cd ..
    sudo rm -rf build
    sudo mkdir /usr/local/lib/googletest
    sudo ln -s /usr/lib/libgtest.a /usr/local/lib/googletest/libgtest.a
    sudo ln -s /usr/lib/libgtest_main.a /usr/local/lib/googletest/libgtest_main.a
```

With Google Test installed, you should be able to build and run the project's tests successfully.

## Expected results

Tested on `Ubuntu 22.04.4 LTS`, `Linux 6.5.0-26-generic #26~22.04.1-Ubuntu SMP PREEMPT_DYNAMIC x86_64 x86_64 x86_64 GNU/Linux`
With **-fsanitize=thread** and **-fsanitize=address** enabled (separately as these two sanitizers cannot work together).
GCC version: `11.4.0`

```
./tests
Running main() from ./googletest/src/gtest_main.cc
[==========] Running 23 tests from 3 test suites.
[----------] Global test environment set-up.
[----------] 11 tests from ThreadPoolTest/0, where TypeParam = std::integral_constant<Strategy, (Strategy)0>
[ RUN      ] ThreadPoolTest/0.SingleTask
[       OK ] ThreadPoolTest/0.SingleTask (1 ms)
[ RUN      ] ThreadPoolTest/0.MultipleTasks
[       OK ] ThreadPoolTest/0.MultipleTasks (1 ms)
[ RUN      ] ThreadPoolTest/0.SideEffectTask
[       OK ] ThreadPoolTest/0.SideEffectTask (13 ms)
[ RUN      ] ThreadPoolTest/0.VaryingExecutionTimes
[       OK ] ThreadPoolTest/0.VaryingExecutionTimes (2253 ms)
[ RUN      ] ThreadPoolTest/0.ExceptionTask
[       OK ] ThreadPoolTest/0.ExceptionTask (1 ms)
[ RUN      ] ThreadPoolTest/0.ReturnComplexType
[       OK ] ThreadPoolTest/0.ReturnComplexType (1 ms)
[ RUN      ] ThreadPoolTest/0.ThreadCount
[       OK ] ThreadPoolTest/0.ThreadCount (2007 ms)
[ RUN      ] ThreadPoolTest/0.TaskDependencies
[       OK ] ThreadPoolTest/0.TaskDependencies (1 ms)
[ RUN      ] ThreadPoolTest/0.ContinueWithSimpleType
[       OK ] ThreadPoolTest/0.ContinueWithSimpleType (1 ms)
[ RUN      ] ThreadPoolTest/0.ChainMultipleContinuations
[       OK ] ThreadPoolTest/0.ChainMultipleContinuations (1 ms)
[ RUN      ] ThreadPoolTest/0.ContinueWithComplexType
[       OK ] ThreadPoolTest/0.ContinueWithComplexType (1 ms)
[----------] 11 tests from ThreadPoolTest/0 (4286 ms total)

[----------] 11 tests from ThreadPoolTest/1, where TypeParam = std::integral_constant<Strategy, (Strategy)1>
[ RUN      ] ThreadPoolTest/1.SingleTask
[       OK ] ThreadPoolTest/1.SingleTask (2 ms)
[ RUN      ] ThreadPoolTest/1.MultipleTasks
[       OK ] ThreadPoolTest/1.MultipleTasks (2 ms)
[ RUN      ] ThreadPoolTest/1.SideEffectTask
[       OK ] ThreadPoolTest/1.SideEffectTask (6 ms)
[ RUN      ] ThreadPoolTest/1.VaryingExecutionTimes
[       OK ] ThreadPoolTest/1.VaryingExecutionTimes (753 ms)
[ RUN      ] ThreadPoolTest/1.ExceptionTask
[       OK ] ThreadPoolTest/1.ExceptionTask (2 ms)
[ RUN      ] ThreadPoolTest/1.ReturnComplexType
[       OK ] ThreadPoolTest/1.ReturnComplexType (1 ms)
[ RUN      ] ThreadPoolTest/1.ThreadCount
[       OK ] ThreadPoolTest/1.ThreadCount (1001 ms)
[ RUN      ] ThreadPoolTest/1.TaskDependencies
[       OK ] ThreadPoolTest/1.TaskDependencies (2 ms)
[ RUN      ] ThreadPoolTest/1.ContinueWithSimpleType
[       OK ] ThreadPoolTest/1.ContinueWithSimpleType (2 ms)
[ RUN      ] ThreadPoolTest/1.ChainMultipleContinuations
[       OK ] ThreadPoolTest/1.ChainMultipleContinuations (2 ms)
[ RUN      ] ThreadPoolTest/1.ContinueWithComplexType
[       OK ] ThreadPoolTest/1.ContinueWithComplexType (3 ms)
[----------] 11 tests from ThreadPoolTest/1 (1783 ms total)

[----------] 1 test from ThreadPoolTests
[ RUN      ] ThreadPoolTests.HighVolume
[       OK ] ThreadPoolTests.HighVolume (310 ms)
[----------] 1 test from ThreadPoolTests (310 ms total)

[----------] Global test environment tear-down
[==========] 23 tests from 3 test suites ran. (6381 ms total)
[  PASSED  ] 23 tests.
```
