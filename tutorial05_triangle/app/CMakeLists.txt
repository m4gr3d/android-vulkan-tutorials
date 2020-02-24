cmake_minimum_required(VERSION 3.4.1)

# build vulkan app
set(SRC_DIR src/main/jni)
set(WRAPPER_DIR ../../common/vulkan_wrapper)

add_library(vktuts SHARED
            ${SRC_DIR}/VulkanMain.cpp
            ${SRC_DIR}/vk_renderer_jni.cpp
            ${WRAPPER_DIR}/vulkan_wrapper.cpp)

include_directories(${WRAPPER_DIR})

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Wall -Werror \
                     -DVK_USE_PLATFORM_ANDROID_KHR")
target_link_libraries(vktuts log android)
