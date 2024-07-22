cmake_minimum_required(VERSION 3.27)
project(SteganografiaObrazowa)

set(CMAKE_CXX_STANDARD 20)

set(BUILD_SHARED_LIBS FALSE)
set(CMAKE_BUILD_TYPE RELEASE)

include(FetchContent)

FetchContent_Declare(
        SFML
        GIT_REPOSITORY  https://github.com/SFML/SFML
        GIT_TAG         2.6.1
)

FetchContent_MakeAvailable(SFML)

include_directories(include)

add_executable(SteganografiaObrazowa
        main.cpp
        crypt.cpp
        fileCheck.cpp)
target_link_libraries(SteganografiaObrazowa sfml-graphics)
