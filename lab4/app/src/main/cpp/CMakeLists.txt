cmake_minimum_required(VERSION 3.22.1)

project("firstlab2")

add_library(spdlog STATIC IMPORTED)
set_target_properties(spdlog PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../../../../../libs/spdlog/build/${ANDROID_ABI}/libspdlog.a)

add_library(
        firstlab2
        SHARED
        native-lib.cpp)

add_library(mbedcrypto SHARED IMPORTED)

set_target_properties(mbedcrypto PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../nativeLibs/${ANDROID_ABI}/libmbedcrypto.so)

target_include_directories(firstlab2 PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/../../../../../libs/spdlog/spdlog/include
        ${CMAKE_CURRENT_SOURCE_DIR}/../../../../../libs/mbedtls/mbedtls/include
)

target_link_libraries(
        firstlab2
        android
        log
        spdlog
        mbedcrypto
        ${log-lib} )